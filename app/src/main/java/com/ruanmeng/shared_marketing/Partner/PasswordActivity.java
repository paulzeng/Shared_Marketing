package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.LoginActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.ActivityStack;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

public class PasswordActivity extends BaseActivity {

    @BindView(R.id.et_pwd_old)
    EditText et_old;
    @BindView(R.id.et_pwd_new)
    EditText et_new;
    @BindView(R.id.et_pwd_confirm)
    EditText et_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        init_title("修改登录密码", "保存");

        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                String old = et_old.getText().toString().trim();
                String pwd = et_new.getText().toString().trim();
                String confirm = et_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(old)) {
                    showToask("请输入当前密码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    showToask("请输入新密码");
                    return;
                }
                if (TextUtils.isEmpty(confirm)) {
                    showToask("请再次输入密码");
                    return;
                }
                if (old.length() < 6) {
                    showToask("当前密码长度不少于6位");
                    return;
                }
                if (pwd.length() < 6) {
                    showToask("新密码长度不少于6位");
                    return;
                }
                if (confirm.length() < 6) {
                    showToask("确认密码长度不少于6位");
                    return;
                }
                if (!pwd.equals(confirm)) {
                    showToask("密码不一致，请重新输入");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.modifyPass, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("old_pass", old);
                mRequest.add("new_pass", confirm);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        PreferencesUtils.putBoolean(baseContext, "isLogin", false);
                        putString("user_id", "");
                        putString("user_type", "");
                        putString("user_name", "");
                        putString("pay_pass", "");
                        putString("gender", "");
                        putString("logo", "");
                        putString("is_vip", "");
                        putString("real_name", "");
                        putString("id_card", "");
                        putString("id_card_img1", "");
                        putString("id_card_img2", "");
                        putString("account", "");
                        putString("commission", "");
                        putString("score", "");
                        putString("unsettle_commission", "");
                        putString("settled_commission", "");
                        putString("is_auth", "");

                        putString("check_result", "");
                        putString("province_id", "");
                        putString("city_id", "");
                        putString("city_name", "");
                        putString("is_open", "1");

                        JPushInterface.stopPush(getApplicationContext());

                        startActivity(LoginActivity.class);
                        ActivityStack.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                    }
                });
                break;
        }
    }
}
