package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.model.MapMessageEvent;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointActivity extends BaseActivity {

    @BindView(R.id.et_appoint_name)
    EditText et_name;
    @BindView(R.id.et_appoint_phone)
    EditText et_phone;
    @BindView(R.id.rg_appoint_gender)
    RadioGroup rg_gender;
    @BindView(R.id.tv_appoint_look)
    TextView tv_look;
    @BindView(R.id.et_appoint_num)
    EditText et_num;
    @BindView(R.id.tv_appoint_start)
    TextView tv_start;
    @BindView(R.id.tv_appoint_end)
    TextView tv_end;
    @BindView(R.id.btn_appoint_ok)
    Button btn_ok;
    @BindView(R.id.tv_appoint_tel)
    TextView tv_tel;

    private int gender = 1;
    private String id_proj, mTel, mLat, mLng;

    private List<BankItem> list_proj = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint);
        ButterKnife.bind(this);
        init_title("一键约车");

        EventBus.getDefault().register(this);
    }

    @Override
    public void init_title() {
        super.init_title();
        try {
            JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_order_mobile");

            mTel = arr.getJSONObject(0).getString("value");
            tv_tel.setText(mTel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
        btn_ok.setClickable(false);

        et_name.addTextChangedListener(this);
        et_phone.addTextChangedListener(this);
        tv_look.addTextChangedListener(this);
        et_num.addTextChangedListener(this);
        tv_start.addTextChangedListener(this);
        tv_end.addTextChangedListener(this);
        rg_gender.setOnCheckedChangeListener(this);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.ll_appoint_tel:
                if (mTel == null) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "在线约车电话：" + mTel,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mTel));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.ll_appoint_look:
                Calendar calendar = Calendar.getInstance();
                int year_now = calendar.get(Calendar.YEAR);

                /*DialogHelper.showDateDialog(this, year_now, year_now + 1, true, new DialogHelper.DateCallBack() {
                    @Override
                    public void doWork(int year, int month, int day, String date) {
                        tv_look.setText(date);
                    }
                });*/

                DialogHelper.showDateDialog(
                        this,
                        year_now,
                        year_now + 1,
                        5,
                        "选择时间",
                        true,
                        true,
                        new DialogHelper.DateAllCallBack() {
                            @Override
                            public void doWork(int year, int month, int day, int hour, int minute, String date) {
                                tv_look.setText(date);
                            }
                        }
                );
                break;
            case R.id.ll_appoint_start:
                startActivity(AppointMapActivity.class);
                break;
            case R.id.ll_appoint_end:
                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("proj_list");
                    final List<String> list_item = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_proj.add(new BankItem(object.getString("id"), object.getString("proj_name")));
                        list_item.add(object.getString("proj_name"));
                    }

                    if (list_item.size() == 0) {
                        showToask("暂无可预约项目");
                        return;
                    }

                    DialogHelper.showCustomDialog(baseContext, "选择项目", list_item, false, new DialogHelper.DialogCallBack() {
                        @Override
                        public void doWork(int postion, String name) {
                            id_proj = list_proj.get(postion).getId();
                            tv_end.setText(name);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_appoint_ok:
                String name = et_name.getText().toString().trim();
                String phone = et_phone.getText().toString().trim();
                String num = et_num.getText().toString().trim();
                if (!CommonUtil.isMobileNumber(phone)) {
                    showToask("手机号格式不正确");
                    return;
                }

                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", name);
                    obj.put("mobile", phone);
                    obj.put("gender", gender);
                    obj.put("departure_place", tv_start.getText().toString());
                    obj.put("departure_lat", mLat);
                    obj.put("departure_lng", mLng);
                    obj.put("destination_place_id", id_proj);
                    obj.put("destination_place", tv_end.getText().toString());
                    obj.put("number_of_person", num);
                    obj.put("pick_up_time", tv_look.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRequest = NoHttp.createStringRequest(HttpIP.bookCar, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("info", obj.toString());

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        onBackPressed();
                    }
                });
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(et_name.getText().toString())
                && !TextUtils.isEmpty(et_phone.getText().toString())
                && !TextUtils.isEmpty(tv_look.getText().toString())
                && !TextUtils.isEmpty(et_num.getText().toString())
                && !TextUtils.isEmpty(tv_start.getText().toString())
                && !TextUtils.isEmpty(tv_end.getText().toString())) {
            btn_ok.setBackgroundResource(R.drawable.btn_bg_selector);
            btn_ok.setClickable(true);
        } else {
            btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
            btn_ok.setClickable(false);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        gender = checkedId == R.id.rb_appoint_male ? 1 : 2;
    }

    @Subscribe
    public void onMessageEvent(MapMessageEvent event) {
        mLat = event.getLat();
        mLng = event.getLng();
        tv_start.setText(event.getName());
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().unregister(this);  //反注册eventBus
        super.onBackPressed();
    }
}
