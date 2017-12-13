package com.ruanmeng.shared_marketing;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.utils.CommonUtil;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgetActivity extends BaseActivity {

    @BindView(R.id.et_forget_phone)
    EditText et_phone;
    @BindView(R.id.et_forget_yzm)
    EditText et_yzm;
    @BindView(R.id.btn_forget_yzm)
    Button btn_yzm;
    @BindView(R.id.et_forget_password)
    EditText et_pwd;
    @BindView(R.id.cb_forget_pwd)
    CheckBox cb_pwd;
    @BindView(R.id.btn_forget_sign)
    Button btn_save;

    // 验证码
    private int time_count;
    private Runnable thread;
    private String tel;
    private String YZM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ButterKnife.bind(this);
        init_title("找回密码", "登录");
    }

    @Override
    public void init_title() {
        super.init_title();

        btn_save.setBackgroundResource(R.drawable.rec_bg_cccccc);
        btn_save.setClickable(false);

        et_phone.addTextChangedListener(this);
        et_yzm.addTextChangedListener(this);
        et_pwd.addTextChangedListener(this);
        cb_pwd.setOnCheckedChangeListener(this);
    }

    @Override
    public void doClick(View v) {
        final String name = et_phone.getText().toString().trim();
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                onBackPressed();
                break;
            case R.id.btn_forget_yzm:
                if (TextUtils.isEmpty(name)) {
                    showToask("请输入手机号");
                    return;
                }
                if (!CommonUtil.isMobileNumber(name)) {
                    showToask("手机号格式不正确");
                    return;
                }

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
                mRequest.add("mobile", name);
                mRequest.add("type", 2);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        try {
                            btn_yzm.setBackgroundResource(R.drawable.rec_bg_red);
                            btn_yzm.setClickable(false);
                            btn_yzm.setTextColor(getResources().getColor(R.color.white));
                            btn_yzm.post(thread);

                            YZM = data.getJSONObject("data").getString("verify_code");
                            tel = name;
                            if (Const.ISCODE) et_yzm.setText(YZM);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.btn_forget_sign:
                String yzm = et_yzm.getText().toString().trim();
                String pwd = et_pwd.getText().toString().trim();
                if (!CommonUtil.isMobileNumber(name)) {
                    showToask("手机号格式不正确");
                    return;
                }
                if (!name.equals(tel)) {
                    showToask("手机号码不匹配，请重新获取验证码");
                    return;
                }
                if (!yzm.equals(YZM)) {
                    showToask("验证码错误，请重新输入");
                    return;
                }
                if (pwd.length() < 6) {
                    showToask("密码长度不少于6位");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.forgetPass, Const.POST);
                mRequest.add("mobile", name);
                mRequest.add("new_pass", pwd);
                mRequest.add("verify_code", YZM);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        onBackPressed();
                    }
                });
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(et_phone.getText().toString())
                && !TextUtils.isEmpty(et_yzm.getText().toString())
                && !TextUtils.isEmpty(et_pwd.getText().toString())) {
            btn_save.setBackgroundResource(R.drawable.btn_bg_selector);
            btn_save.setClickable(true);
        } else {
            btn_save.setBackgroundResource(R.drawable.rec_bg_cccccc);
            btn_save.setClickable(false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            et_pwd.setSelection(et_pwd.getText().length());
        }
        else {
            et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            et_pwd.setSelection(et_pwd.getText().length());
        }
    }

}
