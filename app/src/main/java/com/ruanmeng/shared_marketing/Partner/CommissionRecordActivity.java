package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.graphics.Color;
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
import com.ruanmeng.model.CommissionData;
import com.ruanmeng.model.WithdrawData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommissionRecordActivity extends BaseActivity {

    @BindView(R.id.lv_commission_record_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_commission_record_refresh)
    SwipeRefreshLayout mRefresh;

    private List<WithdrawData.WithdrawInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_record);
        ButterKnife.bind(this);
        init_title("提现记录");

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_hint.setText("暂无提现记录！");

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<WithdrawData.WithdrawInfo>(this, R.layout.item_commission_list, list) {
            @Override
            protected void convert(ViewHolder holder, final WithdrawData.WithdrawInfo info, int position) {
                TextView tv_status = holder.getView(R.id.tv_item_commission_status);
                tv_status.setVisibility(View.VISIBLE);
                switch (info.getOpt_status()) {
                    case "1":
                        tv_status.setText("审核中");
                        tv_status.setBackgroundColor(Color.parseColor("#FF6600"));
                        break;
                    case "2":
                        tv_status.setText("成功");
                        tv_status.setBackgroundColor(Color.parseColor("#00CC00"));
                        break;
                    case "3":
                        tv_status.setText("失败");
                        tv_status.setBackgroundColor(Color.parseColor("#999999"));
                        break;
                }
                holder.setText(R.id.tv_item_commission_title, "提现");
                holder.setText(R.id.tv_item_commission_time, info.getCreate_time());
                holder.setText(R.id.tv_item_commission_rest, "余额：" + info.getLeft_amt());
                holder.setText(R.id.tv_item_commission_reduce, "-" + info.getAmount());
                holder.setText(R.id.tv_item_commission_memo, "备注：" + info.getCheck_result());
                holder.setVisible(R.id.tv_item_commission_memo, !TextUtils.isEmpty(info.getCheck_result()));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, IncomeDetailActivity.class);
                        intent.putExtra("info", info);
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

    @Override
    public void getData(final int pindex) {
        mRequest = NoHttp.createStringRequest(HttpIP.withdrawList, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<WithdrawData>(baseContext, true, WithdrawData.class) {
            @Override
            public void doWork(WithdrawData data, String code) {
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

}
