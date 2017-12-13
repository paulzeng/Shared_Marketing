package com.ruanmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dialog.nohttp.CallServer;
import com.ruanmeng.model.TeamData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Partner.MainActivity;
import com.ruanmeng.shared_marketing.Partner.MemberActivity;
import com.ruanmeng.shared_marketing.Partner.SearchActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DensityUtil;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFragment extends Fragment {

    @BindView(R.id.siv_fragment_team_indicator)
    FixedIndicatorView fixedIndicatorView;
    @BindView(R.id.lv_fragment_team_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_fragment_team_refresh)
    SwipeRefreshLayout mRefresh;

    private String[] items = new String[]{"全部", "二级", "三级"};
    private List<TeamData.TeamList> list = new ArrayList<>();
    private List<TeamData.TeamCount> list_count = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter<TeamData.TeamList> adapter;
    private int pageNum = 1, mLevel = 3;
    private boolean isLoadingMore;
    private Request<String> mRequest;

    //调用这个方法切换时不会释放掉Fragment
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        mRefresh.setRefreshing(true);
        getData(pageNum, mLevel);
    }

    private void init() {
        tv_hint.setText("暂无团队信息");
        iv_hint.setImageResource(R.mipmap.not_search);
        setIndicator(fixedIndicatorView);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<TeamData.TeamList>(getActivity(), R.layout.item_team_list, list) {
            @Override
            protected void convert(ViewHolder holder, final TeamData.TeamList info, int position) {
                holder.getView(R.id.tv_item_team_count).setVisibility(View.GONE);

                holder.setText(
                        R.id.tv_item_team_name,
                        TextUtils.isEmpty(info.getReal_name())
                                ? (TextUtils.isEmpty(info.getUser_name()) ? info.getMobile() : info.getUser_name())
                                : info.getReal_name());
                holder.setText(R.id.tv_item_team_phone, info.getMobile());
                holder.setText(R.id.tv_item_team_time, info.getCreate_time());
                holder.setText(R.id.tv_item_team_done, info.getSuccess_num() + "人");
                holder.setText(R.id.tv_item_team_recommend, info.getRec_num() + "人");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.equals(PreferencesUtils.getString(getActivity(), "is_vip"), "2")) {
                            Intent intent = new Intent(getActivity(), MemberActivity.class);
                            intent.putExtra("id", info.getId());
                            intent.putExtra(
                                    "name",
                                    TextUtils.isEmpty(info.getReal_name())
                                            ? (TextUtils.isEmpty(info.getUser_name()) ? info.getMobile() : info.getUser_name())
                                            : info.getReal_name());
                            startActivity(intent);
                        }
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(1, mLevel);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int total = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                // dy > 0 表示向下滑动
                if (lastVisibleItem >= total - 1 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        getData(pageNum, mLevel);
                    }
                }
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mRefresh.isRefreshing();
            }
        });
    }

    private void setIndicator(Indicator indicator) {
        indicator.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.item_tab_top, parent, false);
                }
                TextView textView = (TextView) convertView;
                //用了固定宽度可以避免TextView文字大小变化，tab宽度变化导致tab抖动现象
                textView.setWidth(DensityUtil.dp2px(50));
                textView.setText(items[position]);
                return convertView;
            }
        });

        indicator.setScrollBar(new ColorBar(getActivity(), getResources().getColor(R.color.colorAccent), 5));
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                mRefresh.setRefreshing(true);
                if (mRequest != null && !mRequest.isFinished()) mRequest.cancel();
                if (list.size() > 0) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }
                pageNum = 1;
                getData(pageNum, mLevel = select == 0 ? 3 : select);
            }
        });
        indicator.setCurrentItem(0, true);
    }

    public void getData(final int pindex, int type) {
        mRequest = NoHttp.createStringRequest(HttpIP.teamRecDetail, Const.POST);
        mRequest.add("user_id", ((MainActivity) getActivity()).getString("user_id"));
        mRequest.add("pindex", pindex);
        mRequest.add("level", type);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<TeamData>(getActivity(), true, TeamData.class) {
                    @Override
                    public void doWork(TeamData data, String code) {
                        if (pindex == 1) list.clear();

                        if (data.getData() != null) {

                            list.addAll(data.getData().getTeam_list());
                            adapter.notifyDataSetChanged();

                            if (data.getData().getTeam_count().size() > 0) {
                                list_count.clear();
                                list_count.addAll(data.getData().getTeam_count());

                                for (TeamData.TeamCount item : list_count) {
                                    int pos = list_count.indexOf(item);
                                    pos = pos == 2 ? 0 : (pos + 1);
                                    String str_item = items[pos] + " " + item.getCount();
                                    SpannableString spanText = new SpannableString(str_item);
                                    spanText.setSpan(new ClickableSpan() {

                                        @Override
                                        public void onClick(View widget) {
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            ds.setColor(getResources().getColor(R.color.colorAccent)); // 设置文件颜色
                                            ds.setUnderlineText(false); // 设置下划线
                                        }

                                    }, items[pos].length(), str_item.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                    ((TextView) fixedIndicatorView.getItemView(pos)).setText(spanText);
                                }
                            }

                        }
                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        mRefresh.setRefreshing(false);
                        isLoadingMore = false;

                        try {
                            if (obj.getJSONObject("data").getJSONArray("team_list").length() > 0) {
                                if (pindex == 1) pageNum = pindex;
                                pageNum++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                    }
                }, false);
    }

    @OnClick(R.id.iv_fragment_team_search)
    public void onClick() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("type", "2");
        startActivity(intent);
    }

}
