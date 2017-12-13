package com.ruanmeng.shared_marketing.Partner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CustomerMessageEvent;
import com.ruanmeng.model.DetailData;
import com.ruanmeng.model.MessageEvent;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Case.ModifyActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerDetailActivity extends BaseActivity {

    @BindView(R.id.lv_customer_detail_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_customer_detail_refresh)
    SwipeRefreshLayout mRefresh;
    private TextView tv_name1, tv_phone1;
    private TextView tv_name2, tv_phone2;
    private TextView tv_name3, tv_phone3;
    private TextView tv_proj, tv_status, tv_time, tv_add;
    private TextView tv_hu, tv_qu, tv_ye, tv_card, tv_memo;
    private LinearLayout ll_zhu;
    private EditText et_content;
    private Button btn_up, btn_down;
    private ExpandableLayout expand_zhi;

    private List<DetailData.RecordInfo> list = new ArrayList<>();
    private String mStatus, mProject, mMobile, mRecMobile, mSalerMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        ButterKnife.bind(this);
        init_title("客户明细");

        EventBus.getDefault().register(this);

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        String user_type = getString("user_type");
        mStatus = getIntent().getStringExtra("status");
        View header = View.inflate(this, R.layout.header_customer_detail, null);
        View footer = View.inflate(this, R.layout.footer_customer_detail, null);

        tv_name1 = (TextView) header.findViewById(R.id.tv_customer_detail_name_ke);
        tv_phone1 = (TextView) header.findViewById(R.id.tv_customer_detail_phone_ke);
        tv_proj = (TextView) header.findViewById(R.id.tv_customer_detail_project);
        tv_name2 = (TextView) header.findViewById(R.id.tv_customer_detail_name_tui);
        tv_phone2 = (TextView) header.findViewById(R.id.tv_customer_detail_phone_tui);
        tv_status = (TextView) header.findViewById(R.id.tv_customer_detail_status);
        tv_name3 = (TextView) header.findViewById(R.id.tv_customer_detail_name_zhi);
        tv_phone3 = (TextView) header.findViewById(R.id.tv_customer_detail_phone_zhi);
        tv_time = (TextView) header.findViewById(R.id.tv_customer_detail_time);
        tv_add = (TextView) header.findViewById(R.id.tv_customer_detail_add);
        tv_hu = (TextView) header.findViewById(R.id.tv_customer_detail_hu);
        tv_qu = (TextView) header.findViewById(R.id.tv_customer_detail_qu);
        tv_ye = (TextView) header.findViewById(R.id.tv_customer_detail_ye);
        tv_card = (TextView) header.findViewById(R.id.tv_customer_detail_card);
        tv_memo = (TextView) header.findViewById(R.id.tv_customer_detail_beizhu);
        ExpandableLayout expand_status = (ExpandableLayout) header.findViewById(R.id.el_customer_detail_expand);
        ExpandableLayout expand_memo = (ExpandableLayout) header.findViewById(R.id.el_customer_detail_memo);
        expand_zhi = (ExpandableLayout) header.findViewById(R.id.el_customer_detail_zhi);
        tv_add.setVisibility(View.INVISIBLE);

        et_content = (EditText) footer.findViewById(R.id.et_customer_detail_content);
        ll_zhu = (LinearLayout) footer.findViewById(R.id.ll_customer_detail_zhu);
        Button btn_modify = (Button) footer.findViewById(R.id.btn_customer_detail_modify);
        btn_up = (Button) footer.findViewById(R.id.btn_customer_detail_up);
        btn_down = (Button) footer.findViewById(R.id.btn_customer_detail_down);

        switch (user_type) {
            case "1":
                ll_zhu.setVisibility(View.GONE);
                expand_status.expand();
                expand_memo.expand();
                break;
            case "2":
            case "10":
                expand_status.expand();
                expand_memo.expand();
                expand_zhi.collapse();
                break;
            case "8":
            case "9":
                expand_status.expand();
                expand_memo.expand();
                break;
            case "3":
                expand_status.expand();
                expand_memo.expand();
                expand_zhi.collapse();

                if (TextUtils.equals("2", getIntent().getStringExtra("type"))) {
                        btn_modify.setVisibility(View.GONE);
                        tv_add.setVisibility(View.VISIBLE);

                    switch (mStatus) {
                        case "2":
                        case "3":
                            btn_up.setText("去电");
                            btn_down.setText("到访");
                            break;
                        case "4":
                            btn_up.setText("到访");
                            btn_down.setText("认购");
                            break;
                        case "5":
                            btn_up.setVisibility(View.GONE);
                            btn_down.setText("签约");
                            break;
                        case "6":
                            btn_up.setVisibility(View.GONE);
                            btn_down.setText("结佣");
                            break;
                        case "7":
                        case "8":
                            tv_add.setVisibility(View.INVISIBLE);
                            ll_zhu.setVisibility(View.GONE);
                            break;
                    }
                } else {
                    tv_add.setVisibility(View.GONE);
                }
                break;
        }

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CommonAdapter mAdapter = new CommonAdapter<DetailData.RecordInfo>(this, R.layout.item_customer_detail_list, list) {
            @Override
            protected void convert(ViewHolder holder, DetailData.RecordInfo info, int position) {
                holder.setText(R.id.tv_item_customer_detail_name, mProject);
                holder.setText(R.id.tv_item_customer_detail_status, info.getCur_status());
                holder.setText(R.id.tv_item_customer_detail_time,info.getCreate_time());

                TextView tv_memo = holder.getView(R.id.tv_item_customer_detail_memo);

                SpannableString spanText = new SpannableString("备注：" + info.getFollow_memo());
                spanText.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) { }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.light)); // 设置文件颜色
                        ds.setUnderlineText(false); // 设置下划线
                    }

                }, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_memo.setText(spanText);
            }
        };

        headerAndFooterAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        headerAndFooterAdapter.addHeaderView(header);

        if (TextUtils.equals("1", user_type) ||
                (TextUtils.equals("3", user_type)
                        && TextUtils.equals("2", getIntent().getStringExtra("type")))) {
            headerAndFooterAdapter.addFooterView(footer);
        }

        mRecyclerView.setAdapter(headerAndFooterAdapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(1);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int total = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载，各位自由选择
                // dy > 0 表示向下滑动
                if (lastVisibleItem >= total - 1 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        getData(pageNum);
                    }
                }
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mRefresh.isRefreshing();
            }
        });
    }

    @Override
    public void getData(final int pindex) {
        mRequest = NoHttp.createStringRequest(HttpIP.customerDetail, Const.POST);
        mRequest.add("customer_id", getIntent().getStringExtra("id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<DetailData>(baseContext, true, DetailData.class) {
            @Override
            public void doWork(DetailData data, String code) {
                if (pindex == 1) list.clear();

                if (data.getData().getRecord().size() > 0) {
                    list.addAll(data.getData().getRecord());
                    int pos = headerAndFooterAdapter.getItemCount();
                    headerAndFooterAdapter.notifyItemRangeInserted(pos, list.size());
                }

                DetailData.DetailInfo info = data.getData();
                tv_name1.setText(info.getName());
                tv_phone1.setText(info.getMobile());
                tv_proj.setText(info.getProject());
                tv_name2.setText(info.getRec_name());
                if (info.getDept_name() != null && !TextUtils.isEmpty(info.getDept_name()))
                    tv_name2.setText(info.getRec_name() + "(" + info.getDept_name() + ")");
                tv_phone2.setText(info.getRec_mobile());
                tv_name3.setText(info.getSaler_name());
                tv_phone3.setText(info.getSaler_mobile());
                tv_time.setText(info.getCreate_time());
                tv_hu.setText(info.getHouse_type());
                tv_qu.setText(info.getCustomer_location());
                tv_ye.setText(info.getYetai());
                tv_card.setText(info.getId_card_no());
                tv_memo.setText(info.getMemo());

                switch (info.getCus_status()) {
                    case 1:
                        tv_status.setText("未推荐");
                        break;
                    case 2:
                        tv_status.setText("已推荐");
                        break;
                    case 3:
                        tv_status.setText("已去电");
                        break;
                    case 4:
                        tv_status.setText("已到访");
                        break;
                    case 5:
                        tv_status.setText("已认购");
                        break;
                    case 6:
                        tv_status.setText("已签约");
                        break;
                    case 7:
                        tv_status.setText("已结佣");
                        break;
                    case 8:
                        tv_status.setText("已失效");
                        break;
                }

                mProject = info.getProject();
                mMobile = info.getMobile();
                mRecMobile = info.getRec_mobile();
                mSalerMobile = info.getSaler_mobile();

                if (TextUtils.equals("2", info.getProj_type())) {
                    if (TextUtils.equals("8", getString("user_type"))
                            || TextUtils.equals("9", getString("user_type"))
                            || TextUtils.equals("4", getString("user_type"))
                            || TextUtils.equals("5", getString("user_type"))
                            || TextUtils.equals("6", getString("user_type")))
                        expand_zhi.collapse();
                }
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
                isLoadingMore = false;

                try {
                    if (obj.getJSONObject("data").getJSONArray("record").length() > 0) {
                        if (pindex == 1) pageNum = pindex;
                        pageNum++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);

        final String memo = et_content.getText().toString().trim();

        switch (v.getId()) {
            case R.id.ll_customer_detail_phone_ke:
                if (mMobile == null || TextUtils.isEmpty(mMobile)) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "客户电话：" + mMobile,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mMobile));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.ll_customer_detail_phone_tui:
                if (mRecMobile == null || TextUtils.isEmpty(mRecMobile)) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "推荐人电话：" + mRecMobile,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mRecMobile));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.ll_customer_detail_phone_zhi:
                if (mSalerMobile == null || TextUtils.isEmpty(mSalerMobile)) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "置业顾问电话：" + mSalerMobile,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mSalerMobile));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.tv_customer_detail_add:
                int pos = linearLayoutManager.getItemCount() - 2;
                if (pos >= 0) mRecyclerView.smoothScrollToPosition(pos);
                break;
            case R.id.btn_customer_detail_modify:
                if (mProject == null || TextUtils.isEmpty(mProject)) return;

                Intent intent = new Intent(this, ModifyActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("mobile", mSalerMobile);
                startActivity(intent);
                break;
            case R.id.btn_customer_detail_up:
                if (TextUtils.isEmpty(memo)) {
                    showToask("请输入备注信息");
                    return;
                }

                switch (mStatus) {
                    case "2":
                    case "3":
                        setUpdateStatus(HttpIP.call, memo, "");
                        break;
                    case "4":
                        setUpdateStatus(HttpIP.visit, memo, "");
                        break;
                }
                break;
            case R.id.btn_customer_detail_down:
                if (TextUtils.isEmpty(memo)) {
                    showToask("请输入备注信息");
                    return;
                }
                switch (mStatus) {
                    case "2":
                    case "3":
                        setUpdateStatus(HttpIP.visit, memo, "");
                        break;
                    case "4":
                        final BottomBaseDialog dialog = new BottomBaseDialog(baseContext) {

                            private TextView tv_cancel, tv_ok;
                            private EditText et_money;

                            @Override
                            public View onCreateView() {
                                View view = View.inflate(baseContext, R.layout.dialog_customer_money, null);

                                tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_customer_cancle);
                                tv_ok = (TextView) view.findViewById(R.id.tv_dialog_customer_ok);
                                et_money = (EditText) view.findViewById(R.id.et_dialog_customer_money);

                                return view;
                            }

                            @Override
                            public void setUiBeforShow() {
                                tv_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dismiss();
                                    }
                                });

                                tv_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String money = et_money.getText().toString();
                                        if (TextUtils.isEmpty(money)) {
                                            showToask("请输入认购金额");
                                            return;
                                        }

                                        dismiss();

                                        setUpdateStatus(HttpIP.subscribe, memo, money);
                                    }
                                });
                                et_money.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (!TextUtils.isEmpty(s)) {
                                            if (".".equals(s.toString())) {
                                                et_money.setText("");
                                                showToask("输入金额不小于1万元");
                                                return;
                                            }
                                            double input_money = Double.parseDouble(et_money.getText().toString());
                                            if (input_money < 1) {
                                                et_money.setText("");
                                                showToask("输入金额不小于1万元");
                                            }
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
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
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                EditText mEditText = (EditText) dialog.findViewById(R.id.et_dialog_customer_money);
                                mEditText.requestFocus();
                                mEditText.setFocusable(true);
                                mEditText.setFocusableInTouchMode(true);
                                InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputManager.showSoftInput(mEditText, 0);
                            }
                        });
                        dialog.show();
                        break;
                    case "5":
                        setUpdateStatus(HttpIP.signContract, memo, "");
                        break;
                    case "6":
                        setUpdateStatus(HttpIP.receivePayment, memo, "");
                        break;
                }
                break;
        }
    }

    private void setUpdateStatus(final String url, String memo, String money) {
        mRequest = NoHttp.createStringRequest(url, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("customer_id", getIntent().getStringExtra("id"));
        mRequest.add("memo", memo);
        if (!TextUtils.isEmpty(money)) mRequest.add("amount", money);

        getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                if (TextUtils.equals(HttpIP.call, url)
                        && TextUtils.equals("2", mStatus)) {
                    tv_status.setText("已去电");
                    mStatus = "3";
                }

                if (TextUtils.equals(HttpIP.visit, url)
                        && !TextUtils.equals("4", mStatus)) {
                    tv_status.setText("已到访");
                    mStatus = "4";
                    btn_up.setText("到访");
                    btn_down.setText("认购");
                }

                if (TextUtils.equals(HttpIP.subscribe, url)) {
                    tv_status.setText("已认购");
                    mStatus = "5";
                    btn_up.setVisibility(View.GONE);
                    btn_down.setText("签约");
                }

                if (TextUtils.equals(HttpIP.signContract, url)) {
                    tv_status.setText("已签约");
                    mStatus = "6";
                    btn_up.setVisibility(View.GONE);
                    btn_down.setText("结佣");
                }

                if (TextUtils.equals(HttpIP.receivePayment, url)) {
                    tv_status.setText("已结佣");
                    mStatus = "7";
                    tv_add.setVisibility(View.INVISIBLE);
                    ll_zhu.setVisibility(View.GONE);
                }

                mRefresh.setRefreshing(true);
                if (list.size() > 0) {
                    list.clear();
                    headerAndFooterAdapter.notifyDataSetChanged();
                }

                et_content.setText("");
                pageNum = 1;
                getData(pageNum);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        tv_name3.setText(event.getName());
        tv_phone3.setText(event.getPhone());
        mSalerMobile = event.getPhone();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.equals(mStatus, getIntent().getStringExtra("status"))) {
            EventBus.getDefault().post(new CustomerMessageEvent(
                    getString("user_type"),
                    mStatus));
        }
        super.onBackPressed();
    }
}
