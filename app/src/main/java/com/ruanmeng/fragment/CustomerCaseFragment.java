package com.ruanmeng.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import com.ruanmeng.model.CustomerData;
import com.ruanmeng.model.NoticeData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.NoticeDetailActivity;
import com.ruanmeng.shared_marketing.Partner.CustomerDetailActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.ruanmeng.view.AlwaysMarqueeTextView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

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
public class CustomerCaseFragment extends Fragment {

    @BindView(R.id.tv_fragment_customer_location)
    TextView tv_location;
    @BindView(R.id.iv_fragment_customer_msg)
    ImageView iv_msg;
    @BindView(R.id.tv_fragment_customer_gonggao)
    AlwaysMarqueeTextView tv_notice;
    @BindView(R.id.el_fragment_customer_expand)
    ExpandableLayout expand;
    @BindView(R.id.siv_fragment_customer_indicator)
    ScrollIndicatorView scrollIndicatorView;
    @BindView(R.id.lv_fragment_customer_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_fragment_customer_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.ll_fragment_customer_recommend)
    LinearLayout ll_recommend;
    @BindView(R.id.iv_fragment_customer_filter)
    ImageView iv_filter;

    private String[] items = new String[]{ "已推荐", "去电", "到访", "认购", "签约", "回款" };
    private List<CustomerData.CustomerList> list = new ArrayList<>();
    private List<CustomerData.CountList> list_count = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter<CustomerData.CustomerList> adapter;
    private int pageNum = 1, mStatus = 2;
    private boolean isLoadingMore;
    private Request<String> mRequest;

    private String id_notice, user_type;

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
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        getNotice();

        mRefresh.setRefreshing(true);
        getData(pageNum, mStatus);
    }

    @Override
    public void onStart() {
        super.onStart();

        setLocation(PreferencesUtils.getString(getActivity(), "city_name"));
    }

    private void init() {
        tv_hint.setText("暂无客户信息");
        iv_hint.setImageResource(R.mipmap.not_search);

        user_type = PreferencesUtils.getString(getActivity(), "user_type");
        switch (user_type) {
            case "1":
                mStatus = 9;
                items = new String[]{ "全部", "已推荐", "去电", "到访", "认购", "签约", "回款", "失效" };

                ll_recommend.setVisibility(View.GONE);
                iv_filter.setVisibility(View.VISIBLE);
                break;
        }

        setIndicator(scrollIndicatorView);
        setNoticeIcon(false);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CustomerData.CustomerList>(getActivity(), R.layout.item_customer_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CustomerData.CustomerList info, int position) {
                holder.setText(R.id.tv_item_customer_name_1, info.getName());
                holder.setText(R.id.tv_item_customer_phone, info.getMobile());
                holder.setText(R.id.tv_item_customer_time, info.getCreate_time());
                holder.setText(R.id.tv_item_customer_name_2, info.getSaler());
                holder.setText(R.id.tv_item_customer_name_3, info.getProject());

                //状态 1：未推荐 2：已推荐 3：去电 4：到访 5：认购 6：签约 7：回款（结佣） 8：失效
                TextView tv_status = holder.getView(R.id.tv_item_customer_status);
                tv_status.setBackgroundResource(R.color.orange);
                switch (info.getCus_status()) {
                    case 1:
                        tv_status.setText("未推荐");
                        break;
                    case 2:
                        tv_status.setText("已推荐");
                        break;
                    case 3:
                        tv_status.setText("已去电");
                        break;
                    case 4:
                        tv_status.setText("已到访");
                        break;
                    case 5:
                        tv_status.setText("已认购");
                        break;
                    case 6:
                        tv_status.setText("已签约");
                        break;
                    case 7:
                        tv_status.setText("已回款");
                        break;
                    case 8:
                        tv_status.setText("已失效");
                        tv_status.setBackgroundColor(Color.parseColor("#CCCCCC"));
                        break;
                }

                holder.setVisible(
                        R.id.v_item_customer_divider_1,
                        holder.getLayoutPosition() != 0);

                LinearLayout ll_zhi = holder.getView(R.id.ll_item_customer_zhi);
                switch (user_type) {
                    case "7":
                        ll_zhi.setVisibility(TextUtils.equals("2", info.getProj_type()) ? View.INVISIBLE : View.VISIBLE);
                        break;
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
                        intent.putExtra("id", info.getId());
                        startActivity(intent);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(1, mStatus);
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
                        getData(pageNum, mStatus);
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

    public void setLocation(String city_name) {
        tv_location.setText(city_name);
    }

    public void updateList() {
        mRefresh.setRefreshing(true);
        pageNum = 1;
        getData(pageNum, mStatus);
    }

    public void setNoticeIcon(boolean isShown) {
        iv_msg.setImageResource(isShown ? R.mipmap.ico_ann_b : R.mipmap.ico_ann);
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
                // textView.setWidth(CommonUtil.getScreenWidth(getActivity()) / 5);
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
                try {
                    pageNum = 1;
                    getData(pageNum, mStatus = Integer.parseInt(list_count.get(select).getCus_status()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        indicator.setCurrentItem(0, true);
    }

    public void getData(final int pindex, int status) {
        mRequest = NoHttp.createStringRequest(HttpIP.customerList, Const.POST);
        mRequest.add("user_id", PreferencesUtils.getString(getActivity(), "user_id"));
        mRequest.add("pindex", pindex);
        mRequest.add("status", status);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<CustomerData>(getActivity(), true, CustomerData.class) {
                    @Override
                    public void doWork(CustomerData data, String code) {
                        if (pindex == 1) list.clear();

                        list.addAll(data.getData().getCustomer_list());
                        adapter.notifyDataSetChanged();

                        //状态 1：未推荐 2：已推荐 3：去电 4：到访 5：认购 6：签约 7：回款（结佣） 8：失效
                        if (data.getData().getCount_list().size() > 0) {
                            list_count.clear();

                            if (TextUtils.equals("1", user_type)) {
                                int count_all = 0;
                                for (CustomerData.CountList item : data.getData().getCount_list()) {
                                    count_all += Integer.parseInt(item.getNum());
                                }
                                list_count.add(new CustomerData.CountList("9", String.valueOf(count_all)));
                                list_count.addAll(data.getData().getCount_list());
                                list_count.remove(1);
                            } else {
                                list_count.add(data.getData().getCount_list().get(1));
                                list_count.add(data.getData().getCount_list().get(2));
                                list_count.add(data.getData().getCount_list().get(3));
                                list_count.add(data.getData().getCount_list().get(4));
                                list_count.add(data.getData().getCount_list().get(5));
                                list_count.add(data.getData().getCount_list().get(6));
                            }

                            for (CustomerData.CountList item : list_count) {
                                int pos = list_count.indexOf(item);
                                String str_item = items[pos] + " " + item.getNum();
                                SpannableString spanText = new SpannableString(str_item);
                                spanText.setSpan(new ClickableSpan() {

                                    @Override
                                    public void onClick(View widget) { }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(getResources().getColor(R.color.colorAccent)); // 设置文件颜色
                                        ds.setUnderlineText(false); // 设置下划线
                                    }

                                }, items[pos].length(), str_item.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                ((TextView) scrollIndicatorView.getItemView(pos)).setText(spanText);
                            }
                        }

                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        mRefresh.setRefreshing(false);
                        isLoadingMore = false;

                        try {
                            if (obj.getJSONObject("data").getJSONArray("customer_list").length() > 0) {
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

    private void getNotice() {
        mRequest = NoHttp.createStringRequest(HttpIP.noticeList, Const.POST);
        mRequest.add("role_type", PreferencesUtils.getString(getActivity(), "user_type"));
        mRequest.add("pindex", 1);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<NoticeData>(getActivity(), true, NoticeData.class) {
                    @Override
                    public void doWork(NoticeData data, String code) {

                        if (data.getData().size() > 0) {
                            id_notice = data.getData().get(0).getId();
                            tv_notice.setText("公告：" + data.getData().get(0).getTitle());
                        } else
                            expand.collapse();

                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        if (!TextUtils.equals("1", code)) expand.collapse();
                    }
                }, false);
    }

    @OnClick({R.id.iv_fragment_customer_close,
            R.id.tv_fragment_customer_gonggao})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_fragment_customer_close:
                expand.collapse();
                break;
            case R.id.tv_fragment_customer_gonggao:
                if (id_notice != null) {
                    Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                    intent.putExtra("id", id_notice);
                    startActivity(intent);
                }
                break;
        }
    }
}
