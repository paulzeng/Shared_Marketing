package com.ruanmeng.shared_marketing.Partner;

import android.os.Bundle;

import com.allen.library.SuperTextView;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RealRightActivity extends BaseActivity {

    @BindView(R.id.stv_real_right_name)
    SuperTextView tv_name;
    @BindView(R.id.stv_real_right_id)
    SuperTextView tv_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_right);
        ButterKnife.bind(this);
        init_title("实名认证");
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_name.setRightString(CommonUtil.nameReplaceWithStar(getString("real_name")));
        tv_card.setRightString(CommonUtil.idCardReplaceWithStar(getString("id_card")));
    }
}
