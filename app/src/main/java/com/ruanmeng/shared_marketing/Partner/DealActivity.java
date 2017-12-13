package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealActivity extends BaseActivity {

    @BindView(R.id.tv_deal_hint)
    TextView tv_hint;
    @BindView(R.id.et_deal_yzm)
    EditText et_yzm;
    @BindView(R.id.btn_deal_yzm)
    Button btn_yzm;
    @BindView(R.id.et_deal_pwd)
    EditText et_pwd;
    @BindView(R.id.et_deal_confirm)
    EditText et_comfirm;
    @BindView(R.id.btn_deal_ok)
    Button btn_ok;
    @BindView(R.id.ll_deal_yzm)
    LinearLayout ll_yzm;
    @BindView(R.id.v_deal_header)
    View v_header;
    @BindView(R.id.v_deal_yzm_divider)
    View v_divider;

    // 验证码
    private int time_count;
    private Runnable thread;
    private String YZM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        ButterKnife.bind(this);
        init_title("设置交易密码");
    }

    @Override
    public void init_title() {
        super.init_title();

        String mobile = getString("mobile");
        tv_hint.setText("验证码将发送至" + CommonUtil.phoneReplaceWithStar(mobile));

        btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
        btn_ok.setClickable(false);

        if (TextUtils.equals("2", getString("is_virtual_acc"))) {
            tv_hint.setVisibility(View.GONE);
            v_header.setVisibility(View.GONE);
            ll_yzm.setVisibility(View.GONE);
            v_divider.setVisibility(View.GONE);
        } else {
            et_yzm.addTextChangedListener(this);
        }
        et_pwd.addTextChangedListener(this);
        et_comfirm.addTextChangedListener(this);

    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.btn_deal_yzm:
                time_count = 60;
                thread = new Runnable() {
                    public void run() {
                        btn_yzm.setText("(" + time_count + ")秒后重发");
                        if (time_count > 0) {
                            btn_yzm.postDelayed(thread, 1000);
                            time_count--;
                        } else {
                            btn_yzm.setText("获取验证码");
                            btn_yzm.setBackgroundResource(R.drawable.rec_bg_f6f6f6_stroke_d8d8d8);
                            btn_yzm.setClickable(true);
                            btn_yzm.setTextColor(getResources().getColor(R.color.light));
                            time_count = 60;
                        }
                    }
                };

                mRequest = NoHttp.createStringRequest(HttpIP.verifyCode, Const.POST);
                mRequest.add("mobile", getString("mobile"));
                mRequest.add("type", 3);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        try {
                            btn_yzm.setBackgroundResource(R.drawable.rec_bg_red);
                            btn_yzm.setClickable(false);
                            btn_yzm.setTextColor(getResources().getColor(R.color.white));
                            btn_yzm.post(thread);

                            YZM = data.getJSONObject("data").getString("verify_code");
                            if (Const.ISCODE) et_yzm.setText(YZM);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.btn_deal_ok:
                String yzm = et_yzm.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                final String confirm = et_comfirm.getText().toString().trim();
                if (!yzm.equals(YZM) && !TextUtils.equals("2", getString("is_virtual_acc"))) {
                    showToask("验证码错误，请重新输入");
                    return;
                }
                if (pwd.length() != 6 || confirm.length() != 6) {
                    showToask("密码长度应为6位");
                    return;
                }
                if (!pwd.equals(confirm)) {
                    showToask("密码不一致，请重新输入");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.modifyPaypass, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("pay_pass", confirm);
                mRequest.add("verify_code", YZM == null ? "" : YZM);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        putString("pay_pass", confirm);

                        onBackPressed();
                    }
                });
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if ((TextUtils.equals("2", getString("is_virtual_acc")) || !TextUtils.isEmpty(et_yzm.getText().toString()))
                && !TextUtils.isEmpty(et_pwd.getText().toString())
                && !TextUtils.isEmpty(et_comfirm.getText().toString())) {
            btn_ok.setBackgroundResource(R.drawable.btn_bg_selector);
            btn_ok.setClickable(true);
        } else {
            btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
            btn_ok.setClickable(false);
        }
    }

}
