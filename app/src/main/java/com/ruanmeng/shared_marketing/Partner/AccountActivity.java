package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.AnimationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountActivity extends BaseActivity {

    @BindView(R.id.tv_account_money)
    TextView tv_money;
    @BindView(R.id.tv_account_title)
    TextView tv_title;
    @BindView(R.id.btn_account_withdraw)
    Button btn_withdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        transparentStatusBar(false);

        tv_title.setText("账户余额");
        btn_withdraw.setVisibility(TextUtils.equals("1", getString("can_withdraw")) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        AnimationHelper.startIncreaseAnimator(tv_money, Float.parseFloat(getString("account")));
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.btn_account_withdraw:
                startActivity(CommissionListActivity.class);
                break;
        }
    }

}
