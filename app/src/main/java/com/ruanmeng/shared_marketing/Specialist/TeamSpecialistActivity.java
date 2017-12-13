package com.ruanmeng.shared_marketing.Specialist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CustomerData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Adviser.FilterActivity;
import com.ruanmeng.shared_marketing.Partner.CustomerDetailActivity;
import com.ruanmeng.shared_marketing.Partner.SearchActivity;
import com.ruanmeng.shared_marketing.R;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamSpecialistActivity extends BaseActivity {

    @BindView(R.id.siv_team_channel_indicator)
    ScrollIndicatorView scrollIndicatorView;
    @BindView(R.id.lv_team_channel_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_team_channel_refresh)
    SwipeRefreshLayout mRefresh;

    private String[] items = new String[]{ "全部", "已推荐", "去电", "到访", "认购", "签约", "回款", "失效"};
    private List<CustomerData.CustomerList> list = new ArrayList<>();
    private List<CustomerData.CountList> list_count = new ArrayList<>();
    private int mStatus = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_distribution);
        ButterKnife.bind(this);
        transparentStatusBar();
        setToolbarVisibility(false);
        init_title();

        mRefresh.setRefreshing(true);
        getData(pageNum, mStatus);
    }

    @Override
    public void init_title() {
        tvTitle = (TextView) findViewById(R.id.tv_filter_title);
        tvTitle.setText(getIntent().getStringExtra("name") + "客户");

        tv_hint.setText("暂无客户信息");
        iv_hint.setImageResource(R.mipmap.not_search);
        setIndicator(scrollIndicatorView);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
                        tv_status.setBackgroundColor(Color.parseColor("#CCCCCC"));
                        break;
                }

                holder.setVisible(
                        R.id.v_item_customer_divider_1,
                        holder.getLayoutPosition() != 0);

                LinearLayout ll_zhi = holder.getView(R.id.ll_item_customer_zhi);
                ll_zhi.setVisibility(TextUtils.equals("2", info.getProj_type()) ? View.INVISIBLE : View.VISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CustomerDetailActivity.class);
                        intent.putExtra("id", info.getId());
                        startActivity(intent);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(1, mStatus);
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
                        getData(pageNum, mStatus);
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

    private void setIndicator(Indicator indicator) {
        indicator.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_tab_top, parent, false);
                }
                TextView textView = (TextView) convertView;
                //用了固定宽度可以避免TextView文字大小变化，tab宽度变化导致tab抖动现象
                // textView.setWidth(DensityUtil.dp2px(80));
                textView.setText(items[position]);
                return convertView;
            }
        });

        indicator.setScrollBar(new ColorBar(baseContext, getResources().getColor(R.color.colorAccent), 5));
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                mRefresh.setRefreshing(true);
                if (mRequest != null && !mRequest.isFinished()) mRequest.cancel();
                if (list.size() > 0) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                }
                pageNum = 1;
                getData(pageNum, mStatus = Integer.parseInt(list_count.get(select).getCus_status()));
            }
        });
        indicator.setCurrentItem(0, true);
    }

    public void getData(final int pindex, int status) {
        mRequest = NoHttp.createStringRequest(HttpIP.customerList2, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("department_id", getIntent().getStringExtra("id"));
        mRequest.add("pindex", pindex);
        mRequest.add("status", status);

        getRequest(new CustomHttpListener<CustomerData>(baseContext, true, CustomerData.class) {
            @Override
            public void doWork(CustomerData data, String code) {
                if (pindex == 1) list.clear();

                list.addAll(data.getData().getCustomer_list());
                adapter.notifyDataSetChanged();

                if (data.getData().getCount_list().size() > 0) {
                    list_count.clear();
                    list_count.add(data.getData().getCount_list().get(8));
                    list_count.addAll(data.getData().getCount_list());
                    list_count.remove(9);
                    list_count.remove(1);

                    for (CustomerData.CountList item : list_count) {
                        int pos = list_count.indexOf(item);
                        String str_item = items[pos] + " " + item.getNum();
                        SpannableString spanText = new SpannableString(str_item);
                        spanText.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(getResources().getColor(R.color.colorAccent)); // 设置文件颜色
                                ds.setUnderlineText(false); // 设置下划线
                            }

                        }, items[pos].length(), str_item.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        ((TextView) scrollIndicatorView.getItemView(pos)).setText(spanText);
                    }
                }

            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
                isLoadingMore = false;

                try {
                    if (obj.getJSONObject("data").getJSONArray("customer_list").length() > 0) {
                        if (pindex == 1) pageNum = pindex;
                        pageNum++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
            }
        }, false);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        Intent intent = getIntent();
        switch (v.getId()) {
            case R.id.iv_nav_right:
                intent.setClass(this, SearchActivity.class);
                intent.putExtra("type", "5");
                intent.putExtra("isGroup", true);
                startActivity(intent);
                break;
            case R.id.iv_nav_filter:
                intent.setClass(this, FilterActivity.class);
                startActivity(intent);
                break;
        }
    }
}
