package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.share.Const;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.DialogHelper;
import com.ruanmeng.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_about_app)
    TextView tv_app;
    @BindView(R.id.stv_about_tel)
    SuperTextView tv_tel;
    @BindView(R.id.stv_about_email)
    SuperTextView tv_email;

    private String mTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        init_title("关于我们");
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_app.setText(getString(R.string.app_name) + "v" + Tools.getVersion(this));

        try {
            JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_platform_tel");
            mTel = arr.getJSONObject(0).getString("value");
            tv_tel.setRightString(mTel);

            JSONArray arr_email = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_service_email");
            String mEmail = arr_email.getJSONObject(0).getString("value");
            tv_email.setRightString(mEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({ R.id.stv_about_tel,
               R.id.stv_about_agreement })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stv_about_tel:
                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "客服电话：" + mTel,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mTel));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.stv_about_agreement:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "用户协议");
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
        }
    }
}
