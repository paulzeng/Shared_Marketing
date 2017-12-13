package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.model.CityData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendActivity extends BaseActivity {

    @BindView(R.id.et_recommend_name)
    EditText et_name;
    @BindView(R.id.et_recommend_phone)
    EditText et_phone;
    @BindView(R.id.tv_recommend_project)
    TextView tv_proj;
    @BindView(R.id.tv_recommend_hu)
    TextView tv_hu;
    @BindView(R.id.tv_recommend_qu)
    TextView tv_qu;
    @BindView(R.id.tv_recommend_ye)
    TextView tv_ye;
    @BindView(R.id.tv_recommend_zhi)
    TextView tv_zhi;
    @BindView(R.id.et_recommend_id)
    EditText et_id;
    @BindView(R.id.et_recommend_memo)
    EditText et_memo;
    @BindView(R.id.btn_recommend_submit)
    Button btn_ok;

    private String id_proj, id_consultant;
    private List<BankItem> list_proj = new ArrayList<>();
    private List<String> list_qu = new ArrayList<>();

    private List<String> list_zhi = new ArrayList<>();
    private List<BankItem> item_zhi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        ButterKnife.bind(this);
        init_title("推荐客户");
    }

    @Override
    public void init_title() {
        super.init_title();
        ivRight.setVisibility(View.VISIBLE);

        btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
        btn_ok.setClickable(false);

        et_name.addTextChangedListener(this);
        et_phone.addTextChangedListener(this);
        tv_proj.addTextChangedListener(this);

        if (getIntent().getStringExtra("id") != null) {
            id_proj = getIntent().getStringExtra("id");
            tv_proj.setText(getIntent().getStringExtra("title"));
        }
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_nav_right:
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.tv_recommend_info:
                intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "推荐用户说明");
                intent.putExtra("type", "8");
                startActivity(intent);
                break;
            case R.id.ll_recommend_project:
                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("proj_list");
                    final List<String> list_item = new ArrayList<>();

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_proj.add(new BankItem(object.getString("id"), object.getString("proj_name")));
                        list_item.add(object.getString("proj_name"));
                    }

                    if (list_item.size() == 0) {
                        showToask("暂无可选项目");
                        return;
                    }

                    DialogHelper.showCustomDialog(baseContext, "选择项目", list_item, false, new DialogHelper.DialogCallBack() {
                        @Override
                        public void doWork(int postion, String name) {
                            id_proj = list_proj.get(postion).getId();
                            tv_proj.setText(name);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_recommend_hu:
                try {
                    JSONArray arr_room = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_house_type_room");
                    JSONArray arr_office = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_house_type_office");
                    JSONArray arr_wc = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_house_type_wc");
                    final List<String> list_room = new ArrayList<>();
                    final List<String> list_office = new ArrayList<>();
                    final List<String> list_wc = new ArrayList<>();

                    for (int i = 0 ; i < arr_room.length() ; i++) {
                        JSONObject object = arr_room.getJSONObject(i);
                        list_room.add(object.getString("value"));
                    }
                    for (int i = 0 ; i < arr_office.length() ; i++) {
                        JSONObject object = arr_office.getJSONObject(i);
                        list_office.add(object.getString("value"));
                    }
                    for (int i = 0 ; i < arr_wc.length() ; i++) {
                        JSONObject object = arr_wc.getJSONObject(i);
                        list_wc.add(object.getString("value"));
                    }

                    if (list_room.size() == 0
                            && list_office.size() == 0
                            && list_wc.size() == 0) {
                        showToask("暂无可选户型");
                        return;
                    }

                    List<List<String>> list_item = new ArrayList<>();
                    if (list_room.size() > 0) list_item.add(list_room);
                    if (list_office.size() > 0) list_item.add(list_office);
                    if (list_wc.size() > 0) list_item.add(list_wc);

                    DialogHelper.showMultiDialog(baseContext, "选择户型", list_item, false, new DialogHelper.MultiDialogCallBack() {
                        @Override
                        public void doWork(List<Integer> potisions, List<String> names) {
                            StringBuilder str = new StringBuilder();
                            for (String item : names) {
                                str.append(item);
                            }
                            tv_hu.setText(str.toString());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_recommend_qu:
                mRequest = NoHttp.createStringRequest(HttpIP.getDistrictList, Const.POST);
                mRequest.add("city_id", getString("city_id"));

                getRequest(new CustomHttpListener<CityData>(baseContext, true, CityData.class) {
                    @Override
                    public void doWork(CityData data, String code) {
                        list_qu.add("不限");

                        if (data.getData().size() > 0) {
                            for (CityData.CityInfo item : data.getData()) {
                                list_qu.add(item.getName());
                            }
                        }

                        DialogHelper.showCustomDialog(baseContext, "选择区域", list_qu, false, new DialogHelper.DialogCallBack() {
                            @Override
                            public void doWork(int postion, String name) {
                                tv_qu.setText(name);
                            }
                        });
                    }
                });
                break;
            case R.id.ll_recommend_ye:
                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_yetai");
                    final List<String> list_ye = new ArrayList<>();

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_ye.add(object.getString("value"));
                    }

                    if (list_ye.size() == 0) {
                        showToask("暂无可选业态");
                        return;
                    }

                    DialogHelper.showCustomDialog(baseContext, "选择业态", list_ye, false, new DialogHelper.DialogCallBack() {
                        @Override
                        public void doWork(int postion, String name) {
                            tv_ye.setText(name);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_recommend_zhi:
                if (item_zhi.size() > 0) {

                    for (BankItem item : item_zhi) {
                        list_zhi.add(item.getContent());
                    }

                    DialogHelper.showCustomDialog(baseContext, "选择置业顾问", list_zhi, false, new DialogHelper.DialogCallBack() {
                        @Override
                        public void doWork(int postion, String name) {
                            id_consultant = item_zhi.get(postion).getTitle();
                            tv_zhi.setText(name);
                        }
                    });
                } else {
                    mRequest = NoHttp.createStringRequest(HttpIP.consultantLists, Const.POST);
                    mRequest.add("user_id", getString("user_id"));

                    getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                        @Override
                        public void doWork(JSONObject data, String code) {
                            try {
                                JSONArray arr = data.getJSONArray("data");

                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject object = arr.getJSONObject(i);
                                    item_zhi.add(new BankItem(
                                            object.getString("id"),
                                            object.getString("user_name"),
                                            object.getString("mobile")));
                                }

                                if (item_zhi.size() > 0) {
                                    for (BankItem item : item_zhi) {
                                        list_zhi.add(item.getContent());
                                    }

                                    DialogHelper.showCustomDialog(baseContext, "选择置业顾问", list_zhi, false, new DialogHelper.DialogCallBack() {
                                        @Override
                                        public void doWork(int postion, String name) {
                                            id_consultant = item_zhi.get(postion).getTitle();
                                            tv_zhi.setText(name);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                }
                break;
            case R.id.btn_recommend_submit:
                String name = et_name.getText().toString().trim();
                String phone = et_phone.getText().toString().trim();
                String card = et_id.getText().toString().trim();
                String memo = et_memo.getText().toString().trim();
                String rec_hu = tv_hu.getText().toString().trim();
                String rec_qu = tv_qu.getText().toString().trim();
                String rec_ye = tv_ye.getText().toString().trim();
                if (!CommonUtil.isMobileNumber(phone)) {
                    showToask("手机号格式不正确");
                    return;
                }
                try {
                    if (!TextUtils.isEmpty(card)
                            && !CommonUtil.IDCardValidate(card.toLowerCase())) {
                        showToask("身份证号码错误");
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mRequest = NoHttp.createStringRequest(HttpIP.recCustomer, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("name", name);
                mRequest.add("mobile", phone);
                mRequest.add("project_id", id_proj);

                if (!TextUtils.isEmpty(rec_hu)) mRequest.add("house_type", rec_hu);
                if (!TextUtils.isEmpty(rec_ye)) mRequest.add("yetai", rec_ye);
                if (id_consultant != null) mRequest.add("consultant_id", id_consultant);
                if (!TextUtils.isEmpty(card)) mRequest.add("idcard", card);
                if (!TextUtils.isEmpty(memo)) mRequest.add("memo", memo);

                if (!TextUtils.isEmpty(rec_qu)
                        && !TextUtils.equals("不限", rec_qu))
                    mRequest.add("district", rec_qu);

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
                && !TextUtils.isEmpty(tv_proj.getText().toString())) {
            btn_ok.setBackgroundResource(R.drawable.btn_bg_selector);
            btn_ok.setClickable(true);
        } else {
            btn_ok.setBackgroundResource(R.drawable.rec_bg_cccccc);
            btn_ok.setClickable(false);
        }
    }

}
