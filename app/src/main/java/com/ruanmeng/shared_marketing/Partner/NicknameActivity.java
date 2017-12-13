package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NicknameActivity extends BaseActivity {

    @BindView(R.id.et_nickname_count)
    EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
        ButterKnife.bind(this);
        init_title("设置昵称", "保存");

        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));
        et_name.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10)} );

        et_name.setText(getString("user_name"));
        et_name.setSelection(et_name.getText().length());
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    showToask("请输入昵称");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.modifyNickname, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("nickname", et_name.getText().toString().trim());

                getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        try {
                            String nick_name = data.getJSONObject("data").getString("nick_name");
                            PreferencesUtils.putString(baseContext, "user_name", nick_name);

                            onBackPressed();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onBackPressed();
                    }
                });
                break;
            case R.id.iv_nickname_clear:
                et_name.setText("");
                break;
        }
    }
}
