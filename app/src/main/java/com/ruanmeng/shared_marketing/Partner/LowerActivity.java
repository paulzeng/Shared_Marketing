package com.ruanmeng.shared_marketing.Partner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LowerActivity extends BaseActivity {

    @BindView(R.id.tv_lower_link)
    TextView tv_link;

    private String link, title, content, create_time, real_name, mobile;
    private ArrayList<String> models = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lower);
        ButterKnife.bind(this);
        init_title("发展下级", "发展规则");

        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));

        getData();
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.devTeam, Const.POST);
        mRequest.add("user_id", getString("user_id"));

        getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    link = data.getJSONObject("data").getString("dev_team_url");
                    tv_link.setText(link);

                    JSONObject rules = data.getJSONObject("data").getJSONObject("rules");
                    title = rules.getString("title");
                    content = rules.getString("content");
                    create_time = rules.getString("create_time");

                    JSONObject user_info = data.getJSONObject("data").getJSONObject("user_info");
                    real_name = user_info.getString("real_name");
                    mobile = user_info.getString("mobile");

                    JSONArray arr = data.getJSONObject("data").getJSONArray("models");
                    for (int i = 0 ; i < arr.length() ; i++) {
                        models.add(arr.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void doClick(View v) {
        Intent intent;
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                if (content == null) return;

                intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "发展规则");
                intent.putExtra("type", "6");
                intent.putExtra("name", title);
                intent.putExtra("content", content);
                intent.putExtra("create_time", create_time);
                startActivity(intent);
                break;
            case R.id.btn_lower_generate:
                if (link == null) return;

                intent = new Intent(this, MouldActivity.class);
                intent.putExtra("real_name", real_name);
                intent.putExtra("mobile", mobile);
                intent.putExtra("list", models);
                intent.putExtra("link", link);
                startActivity(intent);
                break;
            case R.id.tv_lower_copy:
                if (link == null) return;

                ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setPrimaryClip(ClipData.newPlainText(null, link));
                String result = cmb.getText().toString();
                if (result.equals(link)) showToask("复制成功");
                break;
        }
    }

}
