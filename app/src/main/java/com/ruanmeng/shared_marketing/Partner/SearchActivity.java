package com.ruanmeng.shared_marketing.Partner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CustomerData;
import com.ruanmeng.model.GrabSearchData;
import com.ruanmeng.model.MainMessageEvent;
import com.ruanmeng.model.SearchData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.Resident.ReviewActivity;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.et_layout_title_search)
    EditText et_search;
    @BindView(R.id.lv_search_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_search_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CustomerData.CustomerList> list = new ArrayList<>();
    private String condition, mValues, mType, user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setToolbarVisibility(false);
        init_title();
    }

    @Override
    public void init_title() {
        user_type = getString("user_type");
        mType = getIntent().getStringExtra("type");

        iv_hint.setImageResource(R.mipmap.not_search);
        ll_hint.setVisibility(View.VISIBLE);
        InputFilter[] filters = {new NameLengthFilter(12)};
        et_search.setFilters(filters);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        switch (mType) {
            case "1":
            case "3":
            case "5":
                tv_hint.setText("暂无客户信息");

                adapter = new CommonAdapter<CustomerData.CustomerList>(this, R.layout.item_customer_list, list) {
                    @Override
                    protected void convert(ViewHolder holder, final CustomerData.CustomerList info, int position) {
                        holder.setText(R.id.tv_item_customer_name_1, info.getName());
                        holder.setText(R.id.tv_item_customer_phone, info.getMobile());
                        holder.setText(R.id.tv_item_customer_time, info.getCreate_time());
                        holder.setText(R.id.tv_item_customer_name_2, info.getSaler());
                        holder.setText(R.id.tv_item_customer_name_3, info.getProject());

                        //状态 1：未推荐 2：已推荐 3：去电 4：到访 5：认购 6：签约 7：回款（结佣） 8：失效
                        TextView tv_status = holder.getView(R.id.tv_item_customer_status);
                        tv_status.setBackgroundResource(R.color.orange);
                        switch (info.getCus_status()) {
                            case 1:
                                tv_status.setText("未推荐");
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
                                tv_status.setBackgroundColor(Color.parseColor("#CCCCCC"));
                                break;
                        }

                        LinearLayout ll_zhi = holder.getView(R.id.ll_item_customer_zhi);
                        switch (user_type) {
                            case "3":
                                ll_zhi.setVisibility(View.INVISIBLE);
                                break;
                            case "2":
                            case "4":
                            case "5":
                            case "6":
                            case "7":
                                ll_zhi.setVisibility(TextUtils.equals("2", info.getProj_type()) ? View.INVISIBLE : View.VISIBLE);
                                break;
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (info.getCus_status() == 1) {
                                    Intent intent = new Intent(mContext, ReviewActivity.class);
                                    intent.putExtra("id", info.getId());
                                    intent.putExtra("status", String.valueOf(info.getCus_status()));
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(mContext, CustomerDetailActivity.class);
                                    intent.putExtra("id", info.getId());
                                    intent.putExtra("status", String.valueOf(info.getCus_status()));
                                    intent.putExtra("type", info.getProj_type());
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                };
                break;
            case "2":
                tv_hint.setText("暂无团队信息");

                adapter = new CommonAdapter<CustomerData.CustomerList>(this, R.layout.item_team_list, list) {
                    @Override
                    protected void convert(ViewHolder holder, final CustomerData.CustomerList info, int position) {
                        holder.getView(R.id.tv_item_team_time).setVisibility(View.GONE);

                        holder.setText(
                                R.id.tv_item_team_name,
                                TextUtils.isEmpty(info.getReal_name())
                                        ? (TextUtils.isEmpty(info.getUser_name()) ? info.getMobile() : info.getUser_name())
                                        : info.getReal_name());
                        holder.setText(R.id.tv_item_team_phone, info.getMobile());
                        holder.setText(R.id.tv_item_team_count, "+" + info.getContribute_commissioin());
                        holder.setText(R.id.tv_item_team_done, info.getSuccess_num() + "人");
                        holder.setText(R.id.tv_item_team_recommend, info.getRec_num() + "人");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.equals(getString("is_vip"), "2")) {
                                    Intent intent = new Intent(baseContext, MemberActivity.class);
                                    intent.putExtra("id", info.getId());
                                    intent.putExtra(
                                            "name",
                                            TextUtils.isEmpty(info.getReal_name())
                                                    ? (TextUtils.isEmpty(info.getUser_name()) ? info.getMobile() : info.getUser_name())
                                                    : info.getReal_name());
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                };
                break;
            case "4":
                tv_hint.setText("暂无抢单信息");

                adapter = new CommonAdapter<CustomerData.CustomerList>(this, R.layout.item_grab_list, list) {
                    @Override
                    protected void convert(ViewHolder holder, final CustomerData.CustomerList info, int position) {
                        holder.setText(R.id.tv_item_grab_name, info.getName());
                        holder.setText(R.id.tv_item_grab_phone, info.getMobile());
                        holder.setText(R.id.tv_item_grab_project, info.getProj_name());
                        holder.setText(R.id.tv_item_grab_recommend, info.getReal_name());

                        holder.setOnClickListener(R.id.tv_item_grab_qiang, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mRequest = NoHttp.createStringRequest(HttpIP.grabCustomer, Const.POST);
                                mRequest.add("user_id", PreferencesUtils.getString(mContext, "user_id"));
                                mRequest.add("customer_id", info.getId());

                                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                                            @Override
                                            public void doWork(JSONObject data, String code) {
                                                int position = list.indexOf(info);
                                                list.remove(position);
                                                adapter.notifyItemRemoved(position);
                                                ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);

                                                EventBus.getDefault().post(new MainMessageEvent(getString("user_type"), "抢单信息更新"));
                                            }

                                            @Override
                                            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                                                try {
                                                    if (TextUtils.equals("0", code)
                                                            && TextUtils.equals("该单已被其他置业顾问抢单成功！", obj.getString("msg"))) {
                                                        mRefresh.setRefreshing(true);

                                                        getData(1);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                };
                break;
        }

        mRecyclerView.setAdapter(adapter);

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

                if (TextUtils.equals("1", mType)
                        || TextUtils.equals("3", mType)
                        || TextUtils.equals("4", mType)
                        || TextUtils.equals("5", mType)) {
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
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mRefresh.isRefreshing();
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“SEARCH”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive())
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    if (!TextUtils.isEmpty(mValues)) {
                        list.clear();
                        adapter.notifyDataSetChanged();

                        condition = mValues;
                        pageNum = 1;
                        mRefresh.setRefreshing(true);
                        getData(pageNum);
                    }
                    return true;
                }
                return false;
            }
        });

        et_search.addTextChangedListener(this);
    }

    @Override
    public void getData(final int pindex) {
        switch (mType) {
            case "1":
            case "3":
                mRequest = NoHttp.createStringRequest(HttpIP.searchCustomer, Const.POST);
                mRequest.add("pindex", pindex);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("keyword", condition);

                if (TextUtils.equals("3", mType)) {
                    if (TextUtils.equals("9", getString("user_type")))
                        mRequest.add("company_user_id", getIntent().getStringExtra("id"));
                    else
                        mRequest.add("company_partner_id", getIntent().getStringExtra("id"));
                }
                break;
            case "2":
                mRequest = NoHttp.createStringRequest(HttpIP.search, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("keyword", condition);
                break;
            case "4":
                mRequest = NoHttp.createStringRequest(HttpIP.Customerlist_new, Const.POST);
                mRequest.add("pindex", pindex);
                mRequest.add("keyword", condition);
                break;
            case "5":
                mRequest = NoHttp.createStringRequest(HttpIP.searchCustomer, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("pindex", pindex);
                mRequest.add("keyword", condition);

                if (getIntent().getBooleanExtra("isGroup", false))
                    mRequest.add("department_id", getIntent().getStringExtra("id"));
                break;
        }

        if (TextUtils.equals("4", mType)) {
            getRequest(new CustomHttpListener<GrabSearchData>(baseContext, true, GrabSearchData.class) {
                @Override
                public void doWork(GrabSearchData data, String code) {
                    if (pindex == 1) list.clear();

                    list.addAll(data.getData().getData());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                    mRefresh.setRefreshing(false);
                    isLoadingMore = false;

                    if (TextUtils.equals("1", code)) {
                        if (pindex == 1) pageNum = pindex;
                        pageNum++;
                    }

                    ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                }
            }, false);
        } else {
            getRequest(new CustomHttpListener<SearchData>(baseContext, true, SearchData.class) {
                @Override
                public void doWork(SearchData data, String code) {
                    if (pindex == 1) list.clear();

                    list.addAll(data.getData());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                    mRefresh.setRefreshing(false);
                    isLoadingMore = false;

                    if (TextUtils.equals("1", code)) {
                        if (pindex == 1) pageNum = pindex;
                        pageNum++;
                    }

                    ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                }
            }, false);
        }
    }

    @Override
    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.tv_layout_title_cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mValues = s.toString();
        if (TextUtils.isEmpty(mValues)) {
            list.clear();
            adapter.notifyDataSetChanged();

            ll_hint.setVisibility(View.VISIBLE);
        }
    }

    private class NameLengthFilter implements InputFilter {

        int MAX_EN;

        NameLengthFilter(int mAX_EN) {
            MAX_EN = mAX_EN;
        }

        @Override
        public CharSequence filter(
                CharSequence source,
                int start,
                int end,
                Spanned dest,
                int dstart,
                int dend) {

            int destCount = dest.toString().length() + getChineseCount(dest.toString());
            int sourceCount = source.toString().length() + getChineseCount(source.toString());
            return destCount + sourceCount > MAX_EN ? "" : source;
        }

        private int getChineseCount(String str) {
            int count = 0;
            Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]"); // unicode编码，判断是否为汉字
            Matcher m = p.matcher(str);
            while (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    count = count + 1;
                }
            }
            return count;
        }
    }

}
