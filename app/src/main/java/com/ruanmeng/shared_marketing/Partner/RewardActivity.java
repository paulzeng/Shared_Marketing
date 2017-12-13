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

import com.bumptech.glide.Glide;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.Specialist.TeamSpecialistActivity;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardActivity extends BaseActivity {

    @BindView(R.id.lv_reward_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_reward_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CommonData.CommonInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        ButterKnife.bind(this);
        init_title("中奖记录");

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_hint.setText("暂无中奖信息");

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CommonData.CommonInfo>(this, R.layout.item_reward_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CommonData.CommonInfo info, int position) {
                holder.setText(R.id.tv_item_reward_time, "领取时间：" + info.getPrize_date());
                holder.setText(
                        R.id.tv_item_reward_title,
                        info.getPrize_name() + "（" + (TextUtils.equals("2", info.getExchange_status()) ? "已兑换" : "未兑换") + "）");

                holder.setVisible(R.id.v_item_reward_divider_1, holder.getLayoutPosition() + 1 != mDatas.size());
                holder.setVisible(R.id.v_item_reward_divider_2, holder.getLayoutPosition() + 1 == mDatas.size());

                ImageView iv_img = holder.getView(R.id.iv_item_reward_img);
                Glide.with(mContext)
                        .load(info.getPrize_img())
                        .placeholder(R.mipmap.not_2) // 等待时的图片
                        .error(R.mipmap.not_2) // 加载失败的图片
                        .crossFade()
                        .into(iv_img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, RewardDetailActivity.class);
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
        mRequest = NoHttp.createStringRequest(HttpIP.prizeList, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<CommonData>(baseContext, true, CommonData.class) {
            @Override
            public void doWork(CommonData data, String code) {
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
