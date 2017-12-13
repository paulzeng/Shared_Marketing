package com.ruanmeng.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dialog.nohttp.CallServer;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Case.AdviserDetailActivity;
import com.ruanmeng.shared_marketing.Case.ModifyActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment {

    @BindView(R.id.tv_fragment_team_title)
    TextView tv_title;
    @BindView(R.id.iv_fragment_team_search)
    ImageView iv_search;
    @BindView(R.id.lv_fragment_manage_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_fragment_manage_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CommonData.CommonInfo> list = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter<CommonData.CommonInfo> adapter;
    private int pageNum = 1;
    private boolean isLoadingMore;

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
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    private void init() {
        tv_title.setText("全部置业顾问");
        tv_hint.setText("暂无置业顾问信息");
        iv_hint.setImageResource(R.mipmap.not_search);
        iv_search.setVisibility(View.GONE);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CommonData.CommonInfo>(getActivity(), R.layout.item_manage_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CommonData.CommonInfo info, int position) {

                holder.setText(R.id.tv_item_manage_name, info.getUser_name());
                holder.setText(R.id.tv_item_manage_phone, info.getMobile());
                holder.setText(R.id.tv_item_manage_qiang, info.getStatus1());
                holder.setText(R.id.tv_item_manage_qu, info.getStatus2());
                holder.setText(R.id.tv_item_manage_lai, info.getStatus3());
                holder.setText(R.id.tv_item_manage_ren, info.getStatus4());
                holder.setText(R.id.tv_item_manage_qian, info.getStatus5());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AdviserDetailActivity.class);
                        intent.putExtra("id", info.getId());
                        intent.putExtra("name", info.getUser_name());
                        startActivity(intent);
                    }
                });

                holder.setOnClickListener(R.id.tv_item_manage_phone, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.showDialog(
                                mContext,
                                "拨打电话",
                                "职业顾问电话：" + info.getMobile(),
                                "取消",
                                "呼叫", new DialogHelper.HintCallBack() {
                                    @Override
                                    public void doWork() {
                                        Intent phoneIntent = new Intent(
                                                "android.intent.action.CALL",
                                                Uri.parse("tel:" + info.getMobile()));
                                        startActivity(phoneIntent);
                                    }
                                });
                    }
                });

                holder.setVisible(R.id.tv_item_manage_trans, false);

                holder.setOnClickListener(R.id.tv_item_manage_trans, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ModifyActivity.class);
                        intent.putExtra("id", info.getId());
                        intent.putExtra("mobile", info.getMobile());
                        intent.putExtra("isTrans", true);
                        startActivity(intent);
                    }
                });

            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(1);
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
                        getData(pageNum);
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

    public void getData(final int pindex) {
        Request<String> mRequest = NoHttp.createStringRequest(HttpIP.consultantList, Const.POST);
        mRequest.add("user_id", PreferencesUtils.getString(getActivity(), "user_id"));
        mRequest.add("pindex", pindex);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<CommonData>(getActivity(), true, CommonData.class) {
                    @Override
                    public void doWork(CommonData data, String code) {
                        if (pindex == 1) list.clear();

                        list.addAll(data.getData());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        mRefresh.setRefreshing(false);
                        isLoadingMore = false;

                        if (TextUtils.equals("1", code)) {
                            if (pindex == 1) pageNum = pindex;
                            pageNum++;
                        }

                        ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                    }
                }, false);
    }

    public void updateList() {
        mRefresh.setRefreshing(true);
        pageNum = 1;
        getData(pageNum);
    }
}
