package com.ruanmeng.shared_marketing.Adviser;

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
import com.ruanmeng.model.CustomerData;
import com.ruanmeng.model.FilterData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Partner.CustomerDetailActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.Resident.ReviewActivity;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterResultActivity extends BaseActivity {

    @BindView(R.id.lv_filter_result_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.tv_filter_result_total)
    TextView tv_total;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_filter_result_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CustomerData.CustomerList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_result);
        ButterKnife.bind(this);
        init_title(getIntent().getStringExtra("name") + "客户");

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_hint.setText("暂无客户信息");
        iv_hint.setImageResource(R.mipmap.not_search);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CustomerData.CustomerList>(this, R.layout.item_customer_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CustomerData.CustomerList info, final int position) {
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
                        tv_status.setText("已结佣");
                        break;
                    case 8:
                        tv_status.setText("已失效");
                        tv_status.setBackgroundColor(Color.parseColor("#CCCCCC"));
                        break;
                }

                LinearLayout ll_zhi = holder.getView(R.id.ll_item_customer_zhi);
                switch (getString("user_type")) {
                    case "2":
                    case "3":
                        ll_zhi.setVisibility(TextUtils.equals("2", info.getProj_type()) ? View.INVISIBLE : View.VISIBLE);
                        break;
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (info.getCus_status() == 1) {
                            Intent intent = new Intent(mContext, ReviewActivity.class);
                            intent.putExtra("id", info.getId());
                            intent.putExtra("status", String.valueOf(info.getCus_status()));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, CustomerDetailActivity.class);
                            intent.putExtra("id", info.getId());
                            intent.putExtra("status", String.valueOf(info.getCus_status()));
                            intent.putExtra("type", info.getProj_type());
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
        mRequest = NoHttp.createStringRequest(HttpIP.searchCustomerByTime, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("pindex", pindex);
        mRequest.add("status", getIntent().getStringExtra("status"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("type"))) {
            mRequest.add("time_type", getIntent().getStringExtra("type"));
        } else {
            mRequest.add("start_time", getIntent().getStringExtra("start"));
            mRequest.add("end_time", getIntent().getStringExtra("end"));
        }

        if (TextUtils.equals("8", getString("user_type"))) {
            mRequest.add("company_partner_id", getIntent().getStringExtra("id"));
        }
        if (TextUtils.equals("9", getString("user_type"))) {
            mRequest.add("company_user_id", getIntent().getStringExtra("id"));
        }
        if (TextUtils.equals("10", getString("user_type"))) {
            mRequest.add("department_id", getIntent().getStringExtra("id"));
        }

        getRequest(new CustomHttpListener<FilterData>(baseContext, true, FilterData.class) {
            @Override
            public void doWork(FilterData data, String code) {
                if (pindex == 1) list.clear();

                list.addAll(data.getData().getList_data());
                adapter.notifyDataSetChanged();

                tv_total.setText("共" + data.getData().getList_count() + "个客户");
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
                isLoadingMore = false;

                try {
                    if (obj.getJSONObject("data").getJSONArray("list_data").length() > 0) {
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
