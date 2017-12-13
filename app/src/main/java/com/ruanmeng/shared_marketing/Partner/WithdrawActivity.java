package com.ruanmeng.shared_marketing.Partner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.jungly.gridpasswordview.GridPasswordView;
import com.jungly.gridpasswordview.PasswordType;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.model.ComissionMessageEvent;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawActivity extends BaseActivity {

    @BindView(R.id.tv_withdraw_bank)
    TextView tv_bank;
    @BindView(R.id.et_withdraw_bank)
    EditText et_bank;
    @BindView(R.id.et_withdraw_card)
    EditText et_card;
    @BindView(R.id.et_withdraw_count)
    EditText et_count;
    @BindView(R.id.tv_withdraw_money)
    TextView tv_money;
    @BindView(R.id.tv_withdraw_rate)
    TextView tv_rate;
    @BindView(R.id.btn_withdraw_ok)
    Button btn_ok;
    @BindView(R.id.cb_withdraw_agree)
    CheckBox cb_agree;

    private double mAccount, withdraw_rate;
    private List<BankItem> list_bank = new ArrayList<>();
    private String id_bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        init_title("提现", "提现记录");

        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));
        et_count.setText(String.format("%.2f", mAccount - mAccount * withdraw_rate / 100));
    }

    @Override
    public void init_title() {
        super.init_title();
        mAccount = Double.parseDouble(getIntent().getStringExtra("amount"));
        tv_money.setText("提现金额：" + mAccount + "元");

        btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
        btn_ok.setClickable(false);
        et_count.setFocusable(false);
        et_count.setFocusableInTouchMode(false);

        tv_bank.addTextChangedListener(this);
        et_bank.addTextChangedListener(this);
        et_card.addTextChangedListener(this);
        et_count.addTextChangedListener(this);

        try {
            // JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_withdraw_less_limit");
            // less_limit = arr.getJSONObject(0).getString("value");

            JSONArray arr_rate = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_commission_tax_rate");
            String value_rate = arr_rate.getJSONObject(0).getString("value");
            if (!TextUtils.isEmpty(value_rate)) {
                withdraw_rate = Double.parseDouble(value_rate);
                tv_rate.setText("提现税率为"+ value_rate +"%");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                startActivity(CommissionRecordActivity.class);
                break;
            case R.id.iv_withdraw_clear:
                et_count.setText("");
                break;
            case R.id.tv_withdraw_xieyi:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "提现协议");
                intent.putExtra("type", "7");
                startActivity(intent);
                break;
            case R.id.ll_withdraw_bank:
                if (et_count.isFocused()) et_count.clearFocus();

                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("bank_list");
                    List<String> list_item = new ArrayList<>();

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_bank.add(new BankItem(object.getString("id"), object.getString("bank_name")));
                        list_item.add(object.getString("bank_name"));
                    }

                    DialogHelper.showCustomDialog(baseContext, "选择收款方式", "完成", list_item, false, new DialogHelper.DialogCallBack() {
                        @Override
                        public void doWork(int postion, String name) {
                            id_bank = list_bank.get(postion).getId();
                            tv_bank.setText(name);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_withdraw_ok:
                final String card = et_card.getText().toString().trim();
                final String count = et_count.getText().toString().trim();
                if(!card.matches("^(\\d{16}|\\d{18}|\\d{19})$")) {
                    showToask("银行卡号格式错误");
                    return;
                }
                /*if (Double.parseDouble(less_limit) > Double.parseDouble(count)) {
                    showToask("最少可提现金额为：" + less_limit + "元");
                    et_count.setText("");
                    return;
                }*/
                if (!cb_agree.isChecked()) {
                    showToask("请同意提现协议");
                    return;
                }

                String isAuth = getString("is_auth");

                switch (isAuth) {
                    case "0":
                        DialogHelper.showDialog(
                                this,
                                "温馨提示",
                                "未进行实名认证，暂无法提现，是否现在去认证？",
                                "取消",
                                "去认证", new DialogHelper.HintCallBack() {
                                    @Override
                                    public void doWork() {
                                        startActivity(RealnameActivity.class);
                                    }
                                });
                        break;
                    case "1":
                        showToask("实名认证信息正在审核中，暂无法提现");
                        break;
                    case "3":
                        DialogHelper.showDialog(
                                this,
                                "温馨提示",
                                "实名认证失败，是否现在去重新认证？",
                                "取消",
                                "去认证", new DialogHelper.HintCallBack() {
                                    @Override
                                    public void doWork() {
                                        startActivity(RealnameActivity.class);
                                    }
                                });
                        break;
                }

                if (!TextUtils.equals("2", isAuth)) return;

                if (TextUtils.isEmpty(getString("pay_pass"))) {
                    DialogHelper.showDialog(
                            this,
                            "温馨提示",
                            "未设置交易密码，暂无法提现，是否现在去设置？",
                            "取消",
                            "去设置", new DialogHelper.HintCallBack() {
                                @Override
                                public void doWork() {
                                    startActivity(DealActivity.class);
                                }
                            });
                } else {
                    final BottomBaseDialog dialog = new BottomBaseDialog(baseContext) {

                        private ImageView iv_close;
                        private TextView tv_txt, tv_forget;
                        private GridPasswordView gpv_pwd;

                        @Override
                        public View onCreateView() {
                            View view = View.inflate(baseContext, R.layout.dialog_withdraw_password, null);

                            iv_close = (ImageView) view.findViewById(R.id.iv_dialog_withdraw_close);
                            tv_txt = (TextView) view.findViewById(R.id.tv_dialog_withdraw_hint);
                            gpv_pwd = (GridPasswordView) view.findViewById(R.id.gpv_dialog_withdraw_pwd);
                            tv_forget = (TextView) view.findViewById(R.id.tv_dialog_withdraw_forget);
                            gpv_pwd.setPasswordType(PasswordType.NUMBER);
                            tv_txt.setText("提现" + count + "元");

                            return view;
                        }

                        @Override
                        public void setUiBeforShow() {
                            gpv_pwd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
                                @Override
                                public void onTextChanged(String psw) {
                                }

                                @Override
                                public void onInputFinish(String psw) {
                                    dismiss();

                                    mRequest = NoHttp.createStringRequest(HttpIP.withdrawV2, Const.POST);
                                    mRequest.add("user_id", getString("user_id"));
                                    mRequest.add("commission_id", getIntent().getStringExtra("id"));
                                    mRequest.add("amount", count);
                                    mRequest.add("bank_id", id_bank);
                                    mRequest.add("opening_bank", et_bank.getText().toString());
                                    mRequest.add("bank_card", card);
                                    mRequest.add("pay_pass", psw);

                                    getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                                        @Override
                                        public void doWork(JSONObject data, String code) {
                                            // mAccount -= Double.parseDouble(count);
                                            // putString("account", String.format("%.2f", mAccount));
                                            // et_count.setText("");
                                            double account = Double.parseDouble(getString("account"));
                                            account -= mAccount;
                                            putString("account", String.format("%.2f", account));

                                            EventBus.getDefault().post(new ComissionMessageEvent("佣金列表更新"));
                                            finish();
                                        }
                                    });
                                }
                            });

                            iv_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss();
                                }
                            });

                            tv_forget.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss();

                                    startActivity(DealActivity.class);
                                }
                            });
                        }
                    };
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    });
                    /*dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            GridPasswordView mEditText = (GridPasswordView) dialog.findViewById(R.id.gpv_dialog_withdraw_pwd);
                            mEditText.requestFocus();
                            mEditText.setFocusable(true);
                            mEditText.setFocusableInTouchMode(true);
                            InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.showSoftInput(mEditText, 0);
                        }
                    });*/
                    dialog.show();
                }
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(tv_bank.getText().toString())
                && !TextUtils.isEmpty(et_bank.getText().toString())
                && !TextUtils.isEmpty(et_card.getText().toString())
                && !TextUtils.isEmpty(et_count.getText().toString())) {
            btn_ok.setBackgroundResource(R.drawable.btn_bg_selector);
            btn_ok.setClickable(true);
        } else {
            btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
            btn_ok.setClickable(false);
        }

        if (et_count.isFocused() && !TextUtils.isEmpty(s)) {
            if (".".equals(s.toString())) {
                et_count.setText("0.");
                et_count.setSelection(et_count.getText().length()); //设置光标的位置
                return;
            }
            double input_money = Double.parseDouble(et_count.getText().toString());
            if (input_money > mAccount) {
                et_count.setText(String.format("%.2f", mAccount));
                et_count.setSelection(et_count.getText().length()); //设置光标的位置
                tv_money.setText("实际到账金额：" + String.format("%.2f", mAccount - mAccount * withdraw_rate / 100) + "元");
            } else {
                if (s.length() > String.format("%.2f", mAccount).length()) {
                    et_count.setText(String.format("%.2f", mAccount));
                    et_count.setSelection(et_count.getText().length()); //设置光标的位置
                    tv_money.setText("实际到账金额：" + String.format("%.2f", mAccount - mAccount * withdraw_rate / 100) + "元");
                }
                tv_money.setText("实际到账金额：" + String.format("%.2f", input_money - input_money * withdraw_rate / 100) + "元");
            }
        }

        if (et_count.isFocused() && TextUtils.isEmpty(s)) {
            tv_money.setText("提现金额：" + getString("account") + "元");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (et_count.isFocused()) {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) {
                if (temp.length() <= 4) {
                    return;
                } else {
                    s.delete(4, 5);
                    return;
                }
            }
            if (temp.length() - posDot - 1 > 2) {
                s.delete(posDot + 3, posDot + 4);
            }
        }
    }

}
