package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.MemberData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DensityUtil;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberActivity extends BaseActivity {

    @BindView(R.id.lv_member_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_member_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.tv_account_title)
    TextView tv_title;
    private TextView tv_count, tv_done, tv_recommend, tv_phone, tv_time;

    private List<MemberData.MemberList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        ButterKnife.bind(this);
        transparentStatusBar(false);

        init_title();

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        tv_title.setText(getIntent().getStringExtra("name"));

        View view = View.inflate(this, R.layout.header_member, null);
        tv_count = (TextView) view.findViewById(R.id.tv_member_count);
        tv_done = (TextView) view.findViewById(R.id.tv_member_done);
        tv_recommend = (TextView) view.findViewById(R.id.tv_member_recommend);
        tv_phone = (TextView) view.findViewById(R.id.tv_member_phone);
        tv_time = (TextView) view.findViewById(R.id.tv_member_time);

        mRefresh.setProgressViewOffset(false, DensityUtil.dp2px(10), DensityUtil.dp2px(80));
        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CommonAdapter mAdapter = new CommonAdapter<MemberData.MemberList>(this, R.layout.item_member_list, list) {
            @Override
            protected void convert(ViewHolder holder, final MemberData.MemberList info, int position) {
                holder.setText(R.id.tv_item_member_money, info.getCommission() + "元");
                holder.setText(R.id.tv_item_member_time, info.getCreate_time());
                holder.setText(R.id.tv_item_member_deal, "成交价格：" + info.getSuccess_amt() + "元");
                holder.setText(R.id.tv_item_member_info, info.getHouse_name());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(baseContext, MemberDetailActivity.class);
                        intent.putExtra("id", info.getId());
                        intent.putExtra("team_id", info.getRec_user_id());
                        intent.putExtra("name", getIntent().getStringExtra("name"));
                        startActivity(intent);
                    }
                });
            }
        };

        headerAndFooterAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        headerAndFooterAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(headerAndFooterAdapter);

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
        mRequest = NoHttp.createStringRequest(HttpIP.vipCommissionList, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("team_member_id", getIntent().getStringExtra("id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<MemberData>(baseContext, true, MemberData.class) {
            @Override
            public void doWork(MemberData data, String code) {
                if (pindex == 1) list.clear();

                if (data.getData() != null) {

                    if (data.getData().getCommission_List().size() > 0) {
                        list.addAll(data.getData().getCommission_List());
                        int pos = headerAndFooterAdapter.getItemCount();
                        headerAndFooterAdapter.notifyItemRangeInserted(pos, list.size());
                    }

                    double total = Double.parseDouble(data.getData().getTotal_commission());
                    tv_count.setText(String.format("%.2f", total));

                    if (data.getData().getTeam_member_info() != null) {
                        MemberData.MemberInfo info = data.getData().getTeam_member_info();
                        tv_done.setText("已成交客户：" + info.getSuccess_num());
                        tv_recommend.setText("推荐客户数：" + info.getRec_num());
                        tv_phone.setText("手机号码：" + info.getMobile());
                        tv_time.setText("注册时间：" + info.getCreate_time());
                    }

                }
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
                isLoadingMore = false;

                try {
                    if (obj.getJSONObject("data").getJSONArray("commission_List").length() > 0) {
                        if (pindex == 1) pageNum = pindex;
                        pageNum++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

}
