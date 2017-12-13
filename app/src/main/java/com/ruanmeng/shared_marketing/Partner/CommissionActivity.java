package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.AnimationHelper;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommissionActivity extends BaseActivity {

    @BindView(R.id.tv_points_count)
    TextView tv_count;
    @BindView(R.id.stv_commission_person)
    SuperTextView tv_person;
    @BindView(R.id.tv_commission_money_1)
    TextView tv_money1;
    @BindView(R.id.tv_commission_team_1)
    TextView tv_team1;
    @BindView(R.id.tv_commission_money_2)
    TextView tv_money2;
    @BindView(R.id.tv_commission_team_2)
    TextView tv_team2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission);
        ButterKnife.bind(this);
        transparentStatusBar(false);

        getData();
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.commissionDetail, Const.POST);
        mRequest.add("user_id", getString("user_id"));

        getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    JSONObject obj = data.getJSONObject("data");

                    AnimationHelper.startIncreaseAnimator(tv_count, Float.parseFloat(obj.getString("total_commission")));
                    tv_person.setRightString(obj.getString("total_count"));

                    JSONArray arr = obj.getJSONArray("team_count");

                    JSONObject obj1 = arr.getJSONObject(0);
                    tv_money1.setText(String.format("%.2f", Double.parseDouble(obj1.getString("total_commision"))));
                    tv_team1.setText("推荐人：" + obj1.getString("count") + "人");

                    JSONObject obj2 = arr.getJSONObject(1);
                    tv_money2.setText(String.format("%.2f", Double.parseDouble(obj2.getString("total_commision"))));
                    tv_team2.setText("推荐人：" + obj2.getString("count") + "人");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_nav_right:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "佣金说明");
                intent.putExtra("type", "5");
                startActivity(intent);
                break;
            case R.id.btn_commission_down:
                startActivity(LowerActivity.class);
                break;
        }
    }

    @OnClick({ R.id.stv_commission_person,
               R.id.stv_commission_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stv_commission_person:
            case R.id.stv_commission_detail:
                startActivity(TeamActivity.class);
                break;
        }
    }

}
