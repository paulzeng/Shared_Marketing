package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.shared_marketing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RealWrongActivity extends BaseActivity {

    @BindView(R.id.tv_real_wrong_reason)
    TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_wrong);
        ButterKnife.bind(this);
        init_title("实名认证");

        tv_result.setText(getString("check_result"));
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.btn_real_wrong_submit:
                startActivity(RealnameActivity.class);

                onBackPressed();
                break;
        }
    }
}
