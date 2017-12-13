package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.PointData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PointsRecordActivity extends BaseActivity {

    @BindView(R.id.lv_points_record_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_points_record_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.rb_points_record_check_1)
    RadioButton rb_check;
    @BindView(R.id.rg_points_record_check)
    RadioGroup rg_check;

    private List<PointData.PointInfo> list_month = new ArrayList<>();
    private List<PointData.PointItem> list = new ArrayList<>();
    private int mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_record);
        ButterKnife.bind(this);
        init_title("积分兑换记录");

        rb_check.performClick();
    }

    @Override
    public void init_title() {
        super.init_title();
        ivRight.setImageResource(R.mipmap.wenhao_wencheng);
        ivRight.setVisibility(View.VISIBLE);
        tv_hint.setText("暂无积分兑换记录！");

        rg_check.setOnCheckedChangeListener(this);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<PointData.PointItem>(this, R.layout.item_points_list, list) {
            @Override
            protected void convert(ViewHolder holder, PointData.PointItem info, int position) {
                holder.setVisible(R.id.tv_item_points_count, false);
                holder.setText(R.id.tv_item_points_time, info.getCreate_time());
                holder.setText(R.id.tv_item_points_title, info.getOpt_type());
                holder.setText(
                        R.id.tv_item_points_num,
                        (TextUtils.equals("1", info.getScore_type()) ? "+" : "-") + info.getScore());

                holder.setText(R.id.tv_item_points_year, info.getCreate_month());
                if (info.getPosition() != -1) {
                    holder.setText(
                            R.id.tv_item_points_get,
                            list_month.get(info.getPosition()).getGet_score());
                    holder.setText(
                            R.id.tv_item_points_use,
                            "使用：" + list_month.get(info.getPosition()).getConsume_score());
                } else {
                    holder.setText(R.id.tv_item_points_get, "0");
                    holder.setText(R.id.tv_item_points_use, "使用：0");
                }

                holder.setVisible(R.id.ll_item_points_headr, info.isShown());
                holder.setVisible(R.id.ll_item_points_footer, info.getPosition() != -1);
                holder.setVisible(R.id.ll_item_points_get, mStatus != 3);
                holder.setVisible(R.id.tv_item_points_use, mStatus != 2);
            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
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
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.myScoreListV2, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("status", mStatus);

        getRequest(new CustomHttpListener<PointData>(baseContext, true, PointData.class) {
            @Override
            public void doWork(PointData data, String code) {
                list.clear();
                list_month.clear();
                list_month.addAll(data.getData());

                for (int i = 0; i < list_month.size(); i++) {
                    for (int j = 0; j < list_month.get(i).getScore_list().size(); j++) {
                        PointData.PointItem item = list_month.get(i).getScore_list().get(j);
                        item.setShown(j == 0);
                        item.setPosition(i);
                        list.add(item);
                    }

                    if (list_month.get(i).getScore_list().size() == 0) {
                        PointData.PointItem item = new PointData.PointItem();
                        item.setShown(true);
                        item.setPosition(-1);
                        item.setCreate_month(list_month.get(i).getMonth());
                        list.add(item);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);

                ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
            }
        }, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        list.clear();
        adapter.notifyDataSetChanged();

        switch (checkedId) {
            case R.id.rb_points_record_check_1:
                mStatus = 1;
                break;
            case R.id.rb_points_record_check_2:
                mStatus = 2;
                break;
            case R.id.rb_points_record_check_3:
                mStatus = 3;
                break;
        }

        mRefresh.setRefreshing(true);
        getData();
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_nav_right:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("title", "积分规则");
                intent.putExtra("type", "4");
                startActivity(intent);
                break;
        }
    }

}
