package com.ruanmeng.shared_marketing.Channel;

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
import com.ruanmeng.model.CommonData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommissionChannelActivity extends BaseActivity {

    @BindView(R.id.lv_commission_channel_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_commission_channel_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CommonData.CommonInfo> list = new ArrayList<>();
    private String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_channel);
        ButterKnife.bind(this);
        init_title("佣金记录");

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        ivRight.setImageResource(R.mipmap.ico_filter);
        ivRight.setPadding(12, 12, 12, 12);
        ivRight.setVisibility(View.VISIBLE);
        tv_hint.setText("暂无佣金记录信息");
        iv_hint.setImageResource(R.mipmap.not_commission);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CommonData.CommonInfo>(this, R.layout.item_commission_channel_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CommonData.CommonInfo info, int position) {
                holder.setText(R.id.tv_item_commission_channel_done, info.getSuccess_amt());
                holder.setText(R.id.tv_item_commission_channel_add, "+" + info.getCommission());
                holder.setText(R.id.tv_item_commission_channel_time, info.getCreate_time());
            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTime = null;
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
        mRequest = NoHttp.createStringRequest(HttpIP.commissionrecord, Const.POST);
        mRequest.add("company_id", getIntent().getStringExtra("id"));
        mRequest.add("pindex", 1);
        if (mTime != null) mRequest.add("commission_date", mTime);

        getRequest(new CustomHttpListener<CommonData>(baseContext, true, CommonData.class) {
            @Override
            public void doWork(CommonData data, String code) {
                if (pindex == 1) list.clear();

                list.addAll(data.getData());
                int pos = adapter.getItemCount();
                adapter.notifyItemRangeInserted(pos, list.size());
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
            case R.id.iv_nav_right:
                Calendar calendar = Calendar.getInstance();
                int year_now = calendar.get(Calendar.YEAR);

                DialogHelper.showDateDialog(
                        this,
                        year_now - 10,
                        year_now, 2,
                        "筛选时间",
                        false,
                        false,
                        new DialogHelper.DateAllCallBack() {
                            @Override
                            public void doWork(int year, int month, int day, int hour, int minute, String date) {
                                mTime = year + "-" + month;

                                if (list.size() > 0) {
                                    list.clear();
                                    adapter.notifyDataSetChanged();
                                }

                                pageNum = 1;
                                mRefresh.setRefreshing(true);
                                getData(pageNum);
                            }
                        });
                break;
        }
    }
}
