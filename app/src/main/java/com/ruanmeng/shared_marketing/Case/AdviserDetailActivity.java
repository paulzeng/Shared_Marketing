package com.ruanmeng.shared_marketing.Case;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CustomerData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Partner.CustomerDetailActivity;
import com.ruanmeng.shared_marketing.R;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdviserDetailActivity extends BaseActivity {

    @BindView(R.id.tv_adviser_detail_hint)
    TextView tv_Hint;
    @BindView(R.id.lv_adviser_detail_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_adviser_detail_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CustomerData.CustomerList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adviser_detail);
        ButterKnife.bind(this);
        init_title("置业顾问详情");

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_Hint.setText(getIntent().getStringExtra("name") + "已抢单的客户");
        tv_hint.setText("暂无客户信息");
        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CustomerData.CustomerList>(this, R.layout.item_customer_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CustomerData.CustomerList info, int position) {
                holder.setText(R.id.tv_item_customer_name_1, info.getName());
                holder.setText(R.id.tv_item_customer_phone, info.getMobile());
                holder.setText(R.id.tv_item_customer_time, info.getCreate_time());
                holder.setText(R.id.tv_item_customer_name_2, info.getReal_name());
                holder.setText(R.id.tv_item_customer_name_3, info.getProj_name());

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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CustomerDetailActivity.class);
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
        mRequest = NoHttp.createStringRequest(HttpIP.consultantDetail, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("consultant_id", getIntent().getStringExtra("id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<CustomerData>(baseContext, true, CustomerData.class) {
            @Override
            public void doWork(CustomerData data, String code) {
                if (pindex == 1) list.clear();

                if (data.getData().getInformation().size() > 0) {
                    list.addAll(data.getData().getInformation());
                    int pos = adapter.getItemCount();
                    adapter.notifyItemRangeInserted(pos, list.size());
                }
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
                isLoadingMore = false;

                try {
                    if (obj.getJSONObject("data").getJSONArray("information").length() > 0) {
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

}
