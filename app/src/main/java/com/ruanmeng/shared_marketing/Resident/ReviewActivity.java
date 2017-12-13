package com.ruanmeng.shared_marketing.Resident;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CustomerMessageEvent;
import com.ruanmeng.model.DetailData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends BaseActivity {

    @BindView(R.id.tv_review_name_ke)
    TextView tv_name1;
    @BindView(R.id.tv_review_phone_ke)
    TextView tv_phone1;
    @BindView(R.id.tv_review_project)
    TextView tv_proj;
    @BindView(R.id.tv_review_name_tui)
    TextView tv_name2;
    @BindView(R.id.tv_review_phone_tui)
    TextView tv_phone2;
    @BindView(R.id.tv_review_status)
    TextView tv_status;
    @BindView(R.id.tv_review_name_zhi)
    TextView tv_name3;
    @BindView(R.id.tv_review_phone_zhi)
    TextView tv_phone3;
    @BindView(R.id.tv_review_time)
    TextView tv_time;
    @BindView(R.id.ll_review_ok)
    LinearLayout ll_ok;
    @BindView(R.id.tv_review_hu)
    TextView tv_hu;
    @BindView(R.id.tv_review_qu)
    TextView tv_qu;
    @BindView(R.id.tv_review_ye)
    TextView tv_ye;
    @BindView(R.id.tv_review_card)
    TextView tv_card;
    @BindView(R.id.tv_review_beizhu)
    TextView tv_memo;

    private String mStatus, mMobile, mRecMobile, mSalerMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        init_title("客户明细");

        getData(1);
    }

    @Override
    public void init_title() {
        super.init_title();
        String user_type = getString("user_type");
        switch (user_type) {
            case "3":
                ll_ok.setVisibility(View.VISIBLE);
                break;
            default:
                ll_ok.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void getData(final int pindex) {
        mRequest = NoHttp.createStringRequest(HttpIP.customerDetail, Const.POST);
        mRequest.add("customer_id", getIntent().getStringExtra("id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<DetailData>(baseContext, true, DetailData.class) {
            @Override
            public void doWork(DetailData data, String code) {
                DetailData.DetailInfo info = data.getData();
                tv_name1.setText(info.getName());
                tv_phone1.setText(info.getMobile());
                tv_proj.setText(info.getProject());
                tv_name2.setText(info.getRec_name());
                if (info.getDept_name() != null && !TextUtils.isEmpty(info.getDept_name()))
                    tv_name2.setText(info.getRec_name() + "(" + info.getDept_name() + ")");
                tv_phone2.setText(info.getRec_mobile());
                tv_name3.setText(info.getSaler_name());
                tv_phone3.setText(info.getSaler_mobile());
                tv_time.setText(info.getCreate_time());
                tv_status.setText("未推荐");
                tv_hu.setText(info.getHouse_type());
                tv_qu.setText(info.getCustomer_location());
                tv_ye.setText(info.getYetai());
                tv_card.setText(info.getId_card_no());
                tv_memo.setText(info.getMemo());

                mStatus = String.valueOf(info.getCus_status());
                mMobile = info.getMobile();
                mRecMobile = info.getRec_mobile();
                mSalerMobile = info.getSaler_mobile();
            }
        });
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.ll_review_phone_ke:
                if (mMobile == null || TextUtils.isEmpty(mMobile)) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "客户电话：" + mMobile,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mMobile));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.ll_review_phone_tui:
                if (mRecMobile == null || TextUtils.isEmpty(mRecMobile)) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "推荐人电话：" + mRecMobile,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mRecMobile));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.ll_review_phone_zhi:
                if (mSalerMobile == null || TextUtils.isEmpty(mSalerMobile)) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "置业顾问电话：" + mSalerMobile,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mSalerMobile));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.btn_review_ok:
                if (mStatus == null || TextUtils.isEmpty(mStatus)) return;

                mRequest = NoHttp.createStringRequest(HttpIP.check, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("customer_id", getIntent().getStringExtra("id"));
                mRequest.add("result", 1);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        EventBus.getDefault().post(new CustomerMessageEvent(
                                getString("user_type"),
                                mStatus));

                        onBackPressed();
                    }
                });
                break;
        }
    }
}
