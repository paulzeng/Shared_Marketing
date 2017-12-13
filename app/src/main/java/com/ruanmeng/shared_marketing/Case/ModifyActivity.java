package com.ruanmeng.shared_marketing.Case;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.model.ManageMessageEvent;
import com.ruanmeng.model.MessageEvent;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DensityUtil;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModifyActivity extends BaseActivity {

    @BindView(R.id.rg_modify_item)
    RadioGroup rg;

    private List<BankItem> list = new ArrayList<>();
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        ButterKnife.bind(this);
        init_title("修改置业顾问", "确定");

        getData();
    }

    @Override
    public void init_title() {
        super.init_title();
        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                if (pos == -1) {
                    showToask("您没有做修改！");
                    return;
                }

                if (getIntent().getBooleanExtra("isTrans", false)) {
                    mRequest = NoHttp.createStringRequest(HttpIP.batchModifyConsultant, Const.POST);
                    mRequest.add("cur_consult_id", getIntent().getStringExtra("id"));
                    mRequest.add("new_consult_id", list.get(pos).getTitle());

                    getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                        @Override
                        public void doWork(JSONObject data, String code) {
                            String id = list.get(pos).getTitle();
                            String mobile = list.get(pos).getCreate_time();
                            assert id != null;
                            assert mobile != null;

                            EventBus.getDefault().post(new ManageMessageEvent(id, mobile));

                            onBackPressed();
                        }
                    });
                } else {
                    mRequest = NoHttp.createStringRequest(HttpIP.modifyConsultant, Const.POST);
                    mRequest.add("user_id", getString("user_id"));
                    mRequest.add("customer_id", getIntent().getStringExtra("id"));
                    mRequest.add("consultant_id", list.get(pos).getTitle());
                    mRequest.add("consultant_name", list.get(pos).getContent());

                    getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                        @Override
                        public void doWork(JSONObject data, String code) {
                            String id = list.get(pos).getTitle();
                            String name = list.get(pos).getContent();
                            String mobile = list.get(pos).getCreate_time();
                            assert id != null;
                            assert name != null;
                            assert mobile != null;

                            EventBus.getDefault().post(new MessageEvent(id, name, mobile));

                            onBackPressed();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.consultantLists, Const.POST);
        mRequest.add("user_id", PreferencesUtils.getString(baseContext, "user_id"));

        getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        try {
                            JSONArray arr = data.getJSONArray("data");

                            for (int i = 0 ; i < arr.length() ; i++) {
                                JSONObject object = arr.getJSONObject(i);
                                list.add(new BankItem(
                                        object.getString("id"),
                                        object.getString("user_name"),
                                        object.getString("mobile")));
                            }

                            for (BankItem item : list) {
                                RadioButton rb = new RadioButton(baseContext);
                                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
                                rb.setLayoutParams(params);
                                rb.setTextAppearance(baseContext, R.style.Font15_gray);
                                rb.setGravity(Gravity.CENTER_VERTICAL);
                                rb.setPadding(DensityUtil.dp2px(10), 0, DensityUtil.dp2px(10), 0);
                                Drawable rightDrawable = getResources().getDrawable(R.drawable.rb_popu_selector);
                                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                                rb.setCompoundDrawables(null, null, rightDrawable, null);
                                rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                                rb.setText(item.getContent());
                                rb.setId(list.indexOf(item));
                                if (TextUtils.equals(
                                        getIntent().getStringExtra("mobile"),
                                        item.getCreate_time()))
                                    rb.setChecked(true);

                                rg.addView(rb);

                                ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
                                View v = new View(baseContext);
                                v.setLayoutParams(param);
                                v.setBackgroundResource(R.color.divider);
                                rg.addView(v);
                            }

                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    pos = checkedId;
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

}
