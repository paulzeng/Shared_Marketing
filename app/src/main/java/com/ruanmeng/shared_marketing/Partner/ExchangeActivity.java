package com.ruanmeng.shared_marketing.Partner;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.jungly.gridpasswordview.GridPasswordView;
import com.jungly.gridpasswordview.PasswordType;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExchangeActivity extends BaseActivity {

    @BindView(R.id.tv_exchange_num)
    TextView tv_num;
    @BindView(R.id.et_exchange_count)
    EditText et_count;
    @BindView(R.id.tv_exchange_money)
    TextView tv_money;
    @BindView(R.id.btn_exchange_ok)
    Button btn_ok;
    @BindView(R.id.tv_exchange_rule)
    TextView tv_rule;

    private int mPoints, point_num, basePoints = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        ButterKnife.bind(this);
        init_title("积分兑换");
    }

    @Override
    public void init_title() {
        super.init_title();
        mPoints = Integer.parseInt(getString("score"));
        tv_num.setText(String.valueOf(mPoints));

        btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
        btn_ok.setClickable(false);

        et_count.addTextChangedListener(this);

        try {
            JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_score_show");
            tv_rule.setText(arr.getJSONObject(0).getString("value"));

            JSONArray arr_score = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_score");
            basePoints = Integer.parseInt(arr_score.getJSONObject(0).getString("value"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_exchange_clear:
                et_count.setText("");
                break;
            case R.id.btn_exchange_ok:
                if (point_num == 0) return;

                if (TextUtils.isEmpty(getString("pay_pass"))) {
                    DialogHelper.showDialog(
                            this,
                            "温馨提示",
                            "未设置交易密码，暂无法兑换积分，是否现在去设置？",
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
                            tv_txt.setText("兑换" + String.format("%.2f", point_num * 1.0 / basePoints) + "元");

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

                                    mRequest = NoHttp.createStringRequest(HttpIP.exchange, Const.POST);
                                    mRequest.add("user_id", getString("user_id"));
                                    mRequest.add("score", point_num);
                                    mRequest.add("pay_pass", psw);

                                    getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                                        @Override
                                        public void doWork(JSONObject data, String code) {
                                            mPoints -= point_num;
                                            tv_num.setText(String.valueOf(mPoints));
                                            putString("score", String.valueOf(mPoints));
                                            et_count.setText("");
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
                            mEditText.setFocusable(true);
                            mEditText.setFocusableInTouchMode(true);
                            mEditText.requestFocus();
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
        if (!TextUtils.isEmpty(et_count.getText().toString())) {
            btn_ok.setBackgroundResource(R.drawable.btn_bg_selector);
            btn_ok.setClickable(true);

            point_num = Integer.parseInt(s.toString());
            if(point_num > mPoints) {
                et_count.setText(String.valueOf(mPoints));
                et_count.setSelection(et_count.getText().length()); //设置光标的位置
                return;
            }

            tv_money.setText(String.format("%.2f", point_num * 1.0 / basePoints) + "元");
        } else {
            btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
            btn_ok.setClickable(false);

            tv_money.setText("0.00元");
        }
    }
}
