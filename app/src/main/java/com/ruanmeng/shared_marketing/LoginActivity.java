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
import android.widget.FrameLayout;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Adviser.MainAdviserActivity;
import com.ruanmeng.shared_marketing.Case.MainCaseActivity;
import com.ruanmeng.shared_marketing.Channel.MainChannelActivity;
import com.ruanmeng.shared_marketing.Distribution.MainDistributionActivity;
import com.ruanmeng.shared_marketing.Driver.MainDriverActivity;
import com.ruanmeng.shared_marketing.Partner.MainActivity;
import com.ruanmeng.shared_marketing.Resident.MainResidentActivity;
import com.ruanmeng.shared_marketing.Specialist.MainSpecialistActivity;
import com.ruanmeng.shared_marketing.Unit.MainUnitActivity;
import com.ruanmeng.utils.BDLocationUtil;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_login_phone)
    EditText et_name;
    @BindView(R.id.et_login_pwd)
    EditText et_pwd;
    @BindView(R.id.cb_login_pwd)
    CheckBox cb_pwd;
    @BindView(R.id.btn_login_denglu)
    Button bt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init_title();
    }

    @Override
    public void init_title() {
        super.init_title();
        ivBack.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.wrong);
        FrameLayout fl_title = findView(R.id.layout_title);
        fl_title.setBackgroundColor(getResources().getColor(R.color.white));

        bt_login.setBackgroundResource(R.drawable.rec_bg_cccccc);
        bt_login.setClickable(false);

        et_name.addTextChangedListener(this);
        et_pwd.addTextChangedListener(this);
        cb_pwd.setOnCheckedChangeListener(this);

        if (!TextUtils.isEmpty(PreferencesUtils.getString(this, "mobile", ""))) {
            et_name.setText(PreferencesUtils.getString(this, "mobile"));
            et_name.setSelection(et_name.getText().length());
        }
    }

    @Override
    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.iv_nav_right:
                onBackPressed();
                break;
            case R.id.btn_login_denglu:
                String name = et_name.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (!CommonUtil.isMobileNumber(name)) {
                    showToask("手机号格式不正确");
                    return;
                }
                if (pwd.length() < 6) {
                    showToask("密码长度不少于6位");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.login, Const.POST);
                mRequest.add("mobile", name);
                mRequest.add("password", pwd);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        /**
                         * {
                         *   "msgcode": 1,
                         *   "msg": "登录成功！",
                         *   "data": {
                         *      "id": "16",
                         *      "user_name": "13617066904",
                         *      "user_type": "6",
                         *      "mobile": "13617066904",
                         *      "gender": "1",
                         *      "province_id": "0",
                         *      "province_name": "",
                         *      "city_id": "0",
                         *      "city_name": "",
                         *      "account": "0.00",
                         *      "logo": "",
                         *      "id_card": "",
                         *      "real_name": "",
                         *      "house_num": "",
                         *      "commission": "0.00",
                         *      "score": "200",
                         *      "cooper_unit": "0",
                         *      "department_id": "0",
                         *      "projects": "",
                         *      "is_vip": "1"
                         *      "can_withdraw": "2"
                         *   }
                         * }
                         */

                        try {
                            JSONObject obj = data.getJSONObject("data");
                            PreferencesUtils.putBoolean(baseContext, "isLogin", true);  //是否登录
                            putString("user_id", obj.getString("id"));                //用户id
                            putString("user_name", obj.getString("user_name"));       //昵称
                            putString("real_name", obj.getString("real_name"));       //真实姓名
                            putString("user_type", obj.getString("user_type"));       //用户类型
                            putString("mobile", obj.getString("mobile"));             //手机号
                            putString("gender", obj.getString("gender"));             //性别
                            putString("logo", obj.getString("logo"));                 //头像
                            putString("id_card", obj.getString("id_card"));           //身份证号
                            putString("account", obj.getString("account"));           //账户余额
                            putString("commission", obj.getString("commission"));     //佣金
                            putString("score", obj.getString("score"));               //积分
                            putString("is_vip", obj.getString("is_vip"));             //是否VIP 1：否，2：是
                            putString("can_withdraw", obj.getString("can_withdraw")); //可否提现

                            if (TextUtils.isEmpty(obj.getString("city_name"))) {
                                putString("province_id", "410000");                     //省份id
                                putString("city_id", "410300");                         //城市id
                                putString("city_name", "洛阳市");                       //城市名称
                            } else {
                                putString("province_id", obj.getString("province_id")); //省份id
                                putString("city_id", obj.getString("city_id"));         //城市id
                                putString("city_name", obj.getString("city_name"));     //城市名称
                            }

                            /*JPushInterface.resumePush(getApplicationContext());
                            JPushInterface.setAlias( //设置别名（先注册）
                                    getApplicationContext(),
                                    "m_" + getString("user_id"),
                                    new TagAliasCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String alias, Set<String> tags) {
                                            Log.i("JPush", responseCode + ":" + alias);
                                        }
                                    });*/

                            switch (obj.getString("user_type")) {
                                case "1": // 1：案场经理
                                    startActivity(MainCaseActivity.class);
                                    break;
                                case "2": // 2：置业顾问
                                    startActivity(MainAdviserActivity.class);
                                    break;
                                case "3": // 3：驻场
                                    startActivity(MainResidentActivity.class);
                                    break;
                                case "4": // 4：员工合伙人
                                    startActivity(MainActivity.class);
                                    break;
                                case "5": // 5：业主合伙人
                                    startActivity(MainActivity.class);
                                    break;
                                case "6": // 6：社会合伙人
                                    startActivity(MainActivity.class);
                                    break;
                                case "7": // 7：合作单位合伙人
                                    startActivity(MainUnitActivity.class);
                                    break;
                                case "8": // 8：渠道经理
                                    startActivity(MainChannelActivity.class);
                                    break;
                                case "9": // 9：分销专员
                                    startActivity(MainDistributionActivity.class);
                                    break;
                                case "10": // 10：渠道专员
                                    startActivity(MainSpecialistActivity.class);
                                    break;
                                case "11": // 11：司机
                                    startActivity(MainDriverActivity.class);
                                    break;
                            }

                            onBackPressed();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.tv_login_forget:
                startActivity(ForgetActivity.class);
                break;
            case R.id.tv_login_signup:
                startActivity(RegisterActivity.class);
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(et_name.getText().toString())
                && !TextUtils.isEmpty(et_pwd.getText().toString())) {
            bt_login.setBackgroundResource(R.drawable.btn_bg_selector);
            bt_login.setClickable(true);
        } else {
            bt_login.setBackgroundResource(R.drawable.rec_bg_cccccc);
            bt_login.setClickable(false);
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

    @Override
    public void onBackPressed() {
        EventBus.getDefault().unregister(this);                        //反注册eventBus
        BDLocationUtil.getInstance(this).stopLocation();               //停止定位服务
        super.onBackPressed();
    }

}
