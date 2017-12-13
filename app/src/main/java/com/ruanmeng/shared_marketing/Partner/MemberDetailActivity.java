package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.MemberData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.yolanda.nohttp.NoHttp;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberDetailActivity extends BaseActivity {

    @BindView(R.id.tv_member_detail_count)
    TextView tv_count;
    @BindView(R.id.tv_member_detail_done)
    TextView tv_done;
    @BindView(R.id.tv_member_detail_recommend)
    TextView tv_recommend;
    @BindView(R.id.tv_member_detail_phone)
    TextView tv_phone;
    @BindView(R.id.tv_member_detail_sign)
    TextView tv_sign;
    @BindView(R.id.tv_member_detail_house)
    TextView tv_house;
    @BindView(R.id.tv_member_detail_deal)
    TextView tv_deal;
    @BindView(R.id.tv_member_detail_time)
    TextView tv_time;
    @BindView(R.id.tv_member_detail_buy)
    TextView tv_buy;
    @BindView(R.id.tv_member_detail_name)
    TextView tv_name;
    @BindView(R.id.tv_member_detail_square)
    TextView tv_square;
    @BindView(R.id.tv_member_detail_total)
    TextView tv_total;
    @BindView(R.id.tv_member_detail_zhi)
    TextView tv_zhi;
    @BindView(R.id.tv_member_detail_percent)
    TextView tv_percent;
    @BindView(R.id.tv_account_title)
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        ButterKnife.bind(this);
        transparentStatusBar(false);

        init_title();

        getData();
    }

    @Override
    public void init_title() {
        tv_title.setText(getIntent().getStringExtra("name"));
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.vipCommissionDetail, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("team_member_id", getIntent().getStringExtra("team_id"));
        mRequest.add("commission_id", getIntent().getStringExtra("id"));

        getRequest(new CustomHttpListener<MemberData>(baseContext, true, MemberData.class) {
            @Override
            public void doWork(MemberData data, String code) {
                if (data.getData() != null) {

                    if (data.getData().getTeam_member_info() != null) {
                        MemberData.MemberInfo info = data.getData().getTeam_member_info();
                        tv_done.setText("已成交客户：" + info.getSuccess_num());
                        tv_recommend.setText("推荐客户数：" + info.getRec_num());
                        tv_phone.setText("手机号码：" + info.getMobile());
                        tv_sign.setText("注册时间：" + info.getCreate_time());
                    }

                    if (data.getData().getCommission_detail().size() > 0) {
                        MemberData.MemberList info = data.getData().getCommission_detail().get(0);
                        tv_count.setText(info.getCommission());
                        tv_house.setText(info.getHouse_name());
                        tv_deal.setText(info.getSuccess_amt() + "元");
                        tv_time.setText(info.getCreate_time());
                        tv_buy.setText(info.getCustomer_name());
                        tv_name.setText(info.getProj_name());
                        tv_square.setText(info.getHouse_area() + "㎡");
                        tv_total.setText(info.getSuccess_amt() + "元");
                        tv_zhi.setText(info.getSaler_name());
                        tv_percent.setText(info.getCommission_rate() * 100 + "%");
                    }
                }
            }
        });
    }

}
