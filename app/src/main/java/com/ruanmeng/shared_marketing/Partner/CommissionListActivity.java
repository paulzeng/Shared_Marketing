package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.ComissionMessageEvent;
import com.ruanmeng.model.CommissionData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommissionListActivity extends BaseActivity {

    @BindView(R.id.lv_commission_balance_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_commission_balance_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CommissionData.CommissionInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_balance);
        ButterKnife.bind(this);
        init_title("佣金列表", "提现记录");

        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));
        EventBus.getDefault().register(this);

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_hint.setText("暂无佣金成交记录！");
        iv_hint.setImageResource(R.mipmap.not_done);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CommissionData.CommissionInfo>(this, R.layout.item_commission_done_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CommissionData.CommissionInfo info, int position) {
                holder.setText(R.id.tv_item_commission_done_cheng, "成交价：" + info.getSuccess_amt());
                holder.setText(R.id.tv_item_commission_done_yong, info.getCommission());
                holder.setText(R.id.tv_item_commission_done_time, "时间：" + info.getCreate_time());

                holder.setOnClickListener(R.id.tv_item_commission_done_draw, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(info.getCommission())) {
                            Intent intent = new Intent(baseContext, WithdrawActivity.class);
                            intent.putExtra("id", info.getId());
                            intent.putExtra("amount", info.getCommission());
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

    @Override
    public void getData(final int pindex) {
        mRequest = NoHttp.createStringRequest(HttpIP.commissionWHList, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<CommissionData>(baseContext, true, CommissionData.class) {
            @Override
            public void doWork(CommissionData data, String code) {
                if (pindex == 1) list.clear();

                if (data.getData().size() > 0) {
                    list.addAll(data.getData());
                    int pos = adapter.getItemCount();
                    adapter.notifyItemRangeInserted(pos, list.size());
                }
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

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                startActivity(CommissionRecordActivity.class);
                break;
        }
    }

    @Subscribe
    public void onListMessageEvent(ComissionMessageEvent event) {
        mRefresh.setRefreshing(true);

        list.clear();
        adapter.notifyDataSetChanged();

        pageNum = 1;
        getData(pageNum);
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().unregister(this);
        super.onBackPressed();
    }
}
