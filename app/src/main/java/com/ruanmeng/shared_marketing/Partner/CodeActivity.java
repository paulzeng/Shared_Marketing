package com.ruanmeng.shared_marketing.Partner;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.ScoreData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.DensityUtil;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CodeActivity extends BaseActivity {

    @BindView(R.id.lv_code_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_code_refresh)
    SwipeRefreshLayout mRefresh;
    private TextView tv_count;

    private List<ScoreData.ScoreList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        ButterKnife.bind(this);
        transparentStatusBar(false);
        init_title();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startIncreaseAnimator(tv_count, Integer.parseInt(getString("score")));

        mRefresh.setRefreshing(true);
        getData();
    }

    @Override
    public void init_title() {
        View view = View.inflate(this, R.layout.header_code, null);
        tv_count = (TextView) view.findViewById(R.id.tv_code_count);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_code_name);
        RoundedImageView iv_img = (RoundedImageView) view.findViewById(R.id.iv_code_img);

        Glide.with(this)
                .load(getString("logo"))
                .placeholder(R.mipmap.personal_a1) // 等待时的图片
                .error(R.mipmap.personal_a1) // 加载失败的图片
                .crossFade()
                .dontAnimate()
                .into(iv_img);
        String userName = getString("user_name");
        String realName = getString("real_name");
        tv_name.setText(TextUtils.isEmpty(realName) ? userName : realName);

        mRefresh.setProgressViewOffset(false, DensityUtil.dp2px(10), DensityUtil.dp2px(80));
        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CommonAdapter mAdapter = new CommonAdapter<ScoreData.ScoreList>(this, R.layout.item_score_img, list) {
            @Override
            protected void convert(ViewHolder holder, final ScoreData.ScoreList info, int position) {
                Glide.with(mContext)
                        .load(info.getCover_img())
                        .placeholder(R.mipmap.not_3) // 等待时的图片
                        .error(R.mipmap.not_3) // 加载失败的图片
                        .crossFade()
                        .dontAnimate()
                        .into((ImageView) holder.getView(R.id.iv_item_score_img));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.equals("1", info.getL_status())) {
                            Intent intent = new Intent(mContext, WebActivity.class);
                            intent.putExtra("title", "浩客通会员抽奖专区");
                            intent.putExtra("type", "10");
                            intent.putExtra("url", info.getUrl());
                            startActivity(intent);
                        } else {
                            showToask("对不起，该活动已经结束");
                        }
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
                getData();
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
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.myScoreV2, Const.POST);
        mRequest.add("user_id", getString("user_id"));

        getRequest(new CustomHttpListener<ScoreData>(baseContext, true, ScoreData.class) {
            @Override
            public void doWork(ScoreData data, String code) {
                putString("score", data.getData().getScore());
                startIncreaseAnimator(tv_count, Integer.parseInt(data.getData().getScore()));

                list.clear();
                list.addAll(data.getData().getGame());
                headerAndFooterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
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
            case R.id.tv_code_apply:
                startActivity(LowerActivity.class);
                break;
            case R.id.tv_code_record:
                startActivity(RewardActivity.class);
                break;
            case R.id.tv_code_count:
                startActivity(PointsRecordActivity.class);
                break;
        }
    }

    /**
     * 数字文本加载动画，默认1000ms
     */
    private void startIncreaseAnimator(final TextView target, final int to) {
        ValueAnimator animator = ValueAnimator.ofInt(0, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                target.setText("我的积分 " + String.valueOf(valueAnimator.getAnimatedValue()));
            }
        });
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

}
