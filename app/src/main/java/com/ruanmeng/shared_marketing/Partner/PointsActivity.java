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
import android.widget.TextView;

import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.PointData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.AnimationHelper;
import com.ruanmeng.utils.DensityUtil;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PointsActivity extends BaseActivity {

    @BindView(R.id.lv_points_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_points_refresh)
    SwipeRefreshLayout mRefresh;
    private TextView tv_count;

    private List<PointData.PointInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        ButterKnife.bind(this);
        transparentStatusBar(false);
        init_title();

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    protected void onStart() {
        super.onStart();

        AnimationHelper.startIncreaseAnimator(tv_count, Integer.parseInt(getString("score")));
    }

    @Override
    public void init_title() {
        View view = View.inflate(this, R.layout.header_points, null);
        tv_count = (TextView) view.findViewById(R.id.tv_points_count);

        mRefresh.setProgressViewOffset(false, DensityUtil.dp2px(10), DensityUtil.dp2px(80));
        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CommonAdapter mAdapter = new CommonAdapter<PointData.PointInfo>(this, R.layout.item_points_list, list) {
            @Override
            protected void convert(ViewHolder holder, PointData.PointInfo info, int position) {
                holder.getView(R.id.tv_item_points_count).setVisibility(View.GONE);
                holder.setText(R.id.tv_item_points_time, info.getCreate_time());
                holder.setText(R.id.tv_item_points_num, "+" + info.getScore());

                switch (info.getOpt_type()) {
                    case "1":
                        holder.setText(R.id.tv_item_points_title, "注册");
                        break;
                    case "2":
                        holder.setText(R.id.tv_item_points_title, "实名认证");
                        break;
                    case "3":
                        holder.setText(R.id.tv_item_points_title, "推荐客户");
                        break;
                    case "4":
                        holder.setText(R.id.tv_item_points_title, "发展一级下线");
                        break;
                    case "5":
                        holder.setText(R.id.tv_item_points_title, "推荐的客户成功购买");
                        break;
                }
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
        mRequest = NoHttp.createStringRequest(HttpIP.scoreList, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<PointData>(baseContext, true, PointData.class) {
            @Override
            public void doWork(PointData data, String code) {
                if (pindex == 1) list.clear();

                if (data.getData().size() > 0) {
                    list.addAll(data.getData());
                    int pos = headerAndFooterAdapter.getItemCount();
                    headerAndFooterAdapter.notifyItemRangeInserted(pos, list.size());
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
            }
        }, false);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_nav_right:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "积分规则");
                intent.putExtra("type", "4");
                startActivity(intent);
                break;
            case R.id.btn_points_exchange:
                startActivity(ExchangeActivity.class);
                break;
            case R.id.btn_register_record:
                startActivity(PointsRecordActivity.class);
                break;
        }
    }

}
