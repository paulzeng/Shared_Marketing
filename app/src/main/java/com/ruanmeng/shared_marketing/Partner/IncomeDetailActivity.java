package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.WithdrawData;
import com.ruanmeng.shared_marketing.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncomeDetailActivity extends BaseActivity {

    @BindView(R.id.tv_income_detail_num)
    TextView tv_status;
    @BindView(R.id.tv_income_detail_type)
    TextView tv_type;
    @BindView(R.id.tv_income_detail_out)
    TextView tv_out;
    @BindView(R.id.tv_income_detail_time)
    TextView tv_time;
    @BindView(R.id.tv_income_detail_rest)
    TextView tv_rest;
    @BindView(R.id.tv_income_detail_bank)
    TextView tv_bank;
    @BindView(R.id.tv_income_detail_account)
    TextView tv_account;
    @BindView(R.id.tv_income_detail_name)
    TextView tv_name;
    @BindView(R.id.tv_income_detail_phone)
    TextView tv_phone;
    @BindView(R.id.tv_income_detail_done)
    TextView tv_done;
    @BindView(R.id.tv_income_detail_memo)
    TextView tv_memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_detail);
        ButterKnife.bind(this);
        init_title("收支详情");
    }

    @Override
    public void init_title() {
        super.init_title();
        WithdrawData.WithdrawInfo info = (WithdrawData.WithdrawInfo) getIntent().getSerializableExtra("info");

        if (info != null) {
            switch (info.getOpt_status()) {
                case "1":
                    tv_status.setText("审核中");
                    break;
                case "2":
                    tv_status.setText("成功");
                    break;
                case "3":
                    tv_status.setText("失败");
                    break;
            }

            tv_type.setText("提现");
            tv_out.setText("-" + info.getAmount());
            tv_time.setText(info.getCreate_time());
            tv_rest.setText(info.getLeft_amt());
            tv_bank.setText(info.getOpening_bank());
            tv_account.setText(info.getAccount());
            tv_name.setText(info.getCustomer_name());
            tv_phone.setText(info.getCustomer_mobile());
            tv_done.setText(info.getSuccess_amt());
            tv_memo.setText(info.getCheck_result());
        }
    }
}
