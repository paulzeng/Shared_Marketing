package com.ruanmeng.shared_marketing.Adviser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends BaseActivity {

    @BindView(R.id.tv_filter_time1)
    TextView tv_time1;
    @BindView(R.id.tv_filter_time2)
    TextView tv_time2;
    @BindView(R.id.tv_filter_status)
    TextView tv_status;

    private String[] items;
    private String mStatus = "9";
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        init_title("客户");

        String user_type = getString("user_type");
        switch (user_type) {
            case "1":
            case "2":
            case "10":
                items = new String[]{ "9全部", "2已推荐", "3去电", "4到访", "5认购", "6签约", "7结佣", "8失效" };
                break;
            case "3":
            case "8":
            case "9":
                items = new String[]{ "9全部", "1未推荐", "2已推荐", "3去电", "4到访", "5认购", "6签约", "7结佣", "8失效" };
                break;
        }
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        Intent intent = getIntent();
        switch (v.getId()) {
            case R.id.btn_filter_time1:
                intent.setClass(this, FilterResultActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", "1");
                intent.putExtra("name", items[mPosition].substring(1));
                startActivity(intent);
                break;
            case R.id.btn_filter_time2:
                intent.setClass(this, FilterResultActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", "2");
                intent.putExtra("name", "全部");
                startActivity(intent);
                break;
            case R.id.btn_filter_time3:
                intent.setClass(this, FilterResultActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", "3");
                intent.putExtra("name", items[mPosition].substring(1));
                startActivity(intent);
                break;
            case R.id.btn_filter_time4:
                intent.setClass(this, FilterResultActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", "4");
                intent.putExtra("name", items[mPosition].substring(1));
                startActivity(intent);
                break;
            case R.id.btn_filter_time5:
                intent.setClass(this, FilterResultActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", "5");
                intent.putExtra("name", items[mPosition].substring(1));
                startActivity(intent);
                break;
            case R.id.ll_filter_time1:
                DialogHelper.showDateDialog(
                        this,
                        "选择起始时间",
                        "2016-01-01",
                        "",
                        new DialogHelper.DateCallBack() {
                            @Override
                            public void doWork(int year, int month, int day, String date) {
                                tv_time1.setText(date);
                                tv_time2.setText("");
                            }
                        });
                break;
            case R.id.ll_filter_time2:
                if (TextUtils.isEmpty(tv_time1.getText().toString())) {
                    showToask("请选择起始时间");
                    return;
                }

                DialogHelper.showDateDialog(
                        this,
                        "选择截止时间",
                        tv_time1.getText().toString(),
                        "",
                        new DialogHelper.DateCallBack() {
                            @Override
                            public void doWork(int year, int month, int day, String date) {
                                tv_time2.setText(date);
                            }
                        });
                break;
            case R.id.ll_filter_status:
                List<String> list_item = new ArrayList<>();

                for (String item : items) {
                    list_item.add(item.substring(1));
                }

                DialogHelper.showCustomDialog(baseContext, "请选择", list_item, false, new DialogHelper.DialogCallBack() {
                    @Override
                    public void doWork(int postion, String name) {
                        mPosition = postion;
                        mStatus = items[postion].substring(0, 1);

                        tv_status.setText(name);
                    }
                });
                break;
            case R.id.btn_filter_ok:
                if (TextUtils.isEmpty(tv_time1.getText().toString())) {
                    showToask("请选择起始时间");
                    return;
                }

                if (TextUtils.isEmpty(tv_time2.getText().toString())) {
                    showToask("请选择截止时间");
                    return;
                }

                intent.setClass(this, FilterResultActivity.class);
                intent.putExtra("status", mStatus);
                intent.putExtra("type", "");
                intent.putExtra("start", tv_time1.getText().toString());
                intent.putExtra("end", tv_time2.getText().toString());
                intent.putExtra("name", items[mPosition].substring(1));
                startActivity(intent);
                break;
        }
    }
}
