package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.shared_marketing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardDetailActivity extends BaseActivity {

    @BindView(R.id.tv_reward_detail_addr)
    TextView tv_addr;
    @BindView(R.id.tv_reward_detail_title)
    TextView tv_title;
    @BindView(R.id.iv_reward_detail_img)
    ImageView iv_img;
    @BindView(R.id.tv_reward_detail_time)
    TextView tv_time;
    @BindView(R.id.tv_reward_detail_pwd)
    TextView tv_pwd;
    @BindView(R.id.tv_reward_detail_dui)
    TextView tv_dui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);
        ButterKnife.bind(this);
        init_title("详情");
    }

    @Override
    public void init_title() {
        super.init_title();
        CommonData.CommonInfo info = (CommonData.CommonInfo) getIntent().getSerializableExtra("info");

        tv_title.setText("恭喜您获得 " + info.getPrize_name());
        tv_time.setText(info.getPrize_date());
        tv_pwd.setText(info.getSn());
        tv_addr.setText("兑换地址：" + info.getPrize_addr());
        tv_dui.setVisibility(TextUtils.equals("2", info.getExchange_status()) ? View.VISIBLE : View.GONE);

        Glide.with(this)
                .load(info.getPrize_img())
                .placeholder(R.mipmap.not_2) // 等待时的图片
                .error(R.mipmap.not_2) // 加载失败的图片
                .crossFade()
                .into(iv_img);
    }
}
