package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.LoginActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.ActivityStack;
import com.ruanmeng.utils.GlideCacheUtil;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.stv_setting_pay)
    SuperTextView tv_pay;
    @BindView(R.id.iv_setting_img)
    ImageView iv_dian;
    @BindView(R.id.el_setting_expand)
    ExpandableLayout expand;
    @BindView(R.id.stv_setting_cache)
    SuperTextView tv_cache;

    private String mCount = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        init_title("系统设置");
    }

    @Override
    public void init_title() {
        super.init_title();
        switch (getString("user_type")) {
            case "4":
            case "5":
            case "6":
                expand.expand();
                break;
            default:
                expand.collapse();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        tv_cache.setRightString(GlideCacheUtil.getInstance().getCacheSize(this));
        tv_pay.setRightString(TextUtils.isEmpty(getString("pay_pass")) ? "未设置" : "已设置");

        getData();
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.feedbackCount, Const.POST);
        mRequest.add("user_id", getString("user_id"));

        getRequest(new CustomHttpListener<JSONObject>(this, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                /**
                 * {"msgcode":1,"msg":"\u64cd\u4f5c\u6210\u529f\uff01","data":{"feedback_count":"11","suggest_count":"3"}}
                 */
                try {
                    mCount = data.getJSONObject("data").getString("feedback_count");
                    String feedback_count = getString("feedback_count");

                    if (feedback_count == null
                            || TextUtils.isEmpty(feedback_count)
                            || Integer.parseInt(feedback_count) == -1) return;

                    if (Integer.parseInt(mCount) > Integer.parseInt(feedback_count))
                        iv_dian.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    @OnClick({ R.id.stv_setting_password,
               R.id.stv_setting_pay,
               R.id.stv_setting_cache,
               R.id.stv_setting_feedback,
               R.id.stv_setting_help,
               R.id.stv_setting_about})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stv_setting_password:
                startActivity(PasswordActivity.class);
                break;
            case R.id.stv_setting_pay:
                startActivity(DealActivity.class);
                break;
            case R.id.stv_setting_cache:
                final MaterialDialog dialog = new MaterialDialog(this);

                dialog.content("确定要清除缓存吗？")
                        .title("清除缓存")
                        .btnText("取消", "确定")
                        .btnTextColor(
                                getResources().getColor(R.color.black),
                                getResources().getColor(R.color.blue))
                        .showAnim(new BounceTopEnter())
                        .dismissAnim(new SlideBottomExit())
                        .show();
                dialog.setOnBtnClickL(
                        new OnBtnClickL() {//left btn click listener
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                            }
                        },
                        new OnBtnClickL() {//right btn click listener
                            @Override
                            public void onBtnClick() {
                                dialog.dismiss();
                                try {
                                    GlideCacheUtil.getInstance().clearImageAllCache(baseContext);
                                    tv_cache.setRightString("0.0B");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
                break;
            case R.id.stv_setting_feedback:
                putString("feedback_count", mCount);
                iv_dian.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(this, FeedbackActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
            case R.id.stv_setting_help:
                startActivity(HelpActivity.class);
                break;
            case R.id.stv_setting_about:
                startActivity(AboutActivity.class);
                break;
        }
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.btn_setting_quit:
                final MaterialDialog materialDialog = new MaterialDialog(this);
                materialDialog.content("确定要退出登录吗")
                        .title("退出登录")
                        .btnText("取消", "确定")
                        .btnTextColor(
                                getResources().getColor(R.color.black),
                                getResources().getColor(R.color.blue))
                        .showAnim(new BounceTopEnter())
                        .show();
                materialDialog.setOnBtnClickL(
                        new OnBtnClickL() { //left btn click listener
                            @Override
                            public void onBtnClick() {
                                materialDialog.dismiss();
                            }
                        },
                        new OnBtnClickL() { //right btn click listener
                            @Override
                            public void onBtnClick() {
                                materialDialog.dismiss();

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

                                putString("feedback_count", "-1");
                                putString("suggest_count", "-1");
                                putString("map_lat", "");
                                putString("map_lng", "");

                                /*Set<String> tagSet = new LinkedHashSet<>();
                                JPushInterface.setAliasAndTags( //设置别名与标签
                                        getApplicationContext(),
                                        null,
                                        tagSet,
                                        new TagAliasCallback() {
                                            @Override
                                            public void gotResult(int responseCode, String alias, Set<String> set) {
                                                Log.i("JPush", responseCode + ":" + set.toString());
                                            }
                                        });*/
                                JPushInterface.stopPush(getApplicationContext());

                                startActivity(LoginActivity.class);
                                ActivityStack.getScreenManager().popAllActivityExceptOne(LoginActivity.class);
                            }
                        });
                break;
        }
    }
}
