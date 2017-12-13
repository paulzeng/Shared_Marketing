package com.ruanmeng.shared_marketing.Distribution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CompanyData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Channel.TeamChannelActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.AnimationHelper;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaleActivity extends BaseActivity {

    @BindView(R.id.tv_sale_num)
    TextView tv_num;
    @BindView(R.id.lv_sale_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_sale_refresh)
    SwipeRefreshLayout mRefresh;

    private List<CompanyData.UserItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        ButterKnife.bind(this);
        init_title(getIntent().getStringExtra("name"));

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_hint.setText("暂无团队信息");
        iv_hint.setImageResource(R.mipmap.not_search);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<CompanyData.UserItem>(this, R.layout.item_company_list, list) {
            @Override
            protected void convert(ViewHolder holder, final CompanyData.UserItem info, int position) {
                holder.setText(R.id.tv_item_company_name, info.getReal_name());
                holder.setText(R.id.tv_item_company_phone, info.getMobile());
                holder.setText(R.id.tv_item_company_recommend, info.getRec_num());

                holder.setVisible(R.id.v_item_company_divider_1, holder.getLayoutPosition() + 1 != mDatas.size());
                holder.setVisible(R.id.v_item_company_divider_2, holder.getLayoutPosition() + 1 == mDatas.size());

                holder.setOnClickListener(R.id.ll_item_company_phone, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (info.getMobile() == null
                                || TextUtils.isEmpty(info.getMobile())) return;

                        DialogHelper.showDialog(
                                mContext,
                                "拨打电话",
                                info.getReal_name() + "电话：" + info.getMobile(),
                                "取消",
                                "呼叫", new DialogHelper.HintCallBack() {
                                    @Override
                                    public void doWork() {
                                        Intent phoneIntent = new Intent(
                                                "android.intent.action.CALL",
                                                Uri.parse("tel:" + info.getMobile()));
                                        startActivity(phoneIntent);
                                    }
                                });
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, TeamDistributionActivity.class);
                        intent.putExtra("id", info.getId());
                        intent.putExtra("name", info.getReal_name());
                        startActivity(intent);
                    }
                });
            }
        };

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
        mRequest = NoHttp.createStringRequest(HttpIP.teamList, Const.POST);
        mRequest.add("company_id", getIntent().getStringExtra("id"));
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<CompanyData>(baseContext, true, CompanyData.class) {
            @Override
            public void doWork(CompanyData data, String code) {
                if (pindex == 1) list.clear();

                list.addAll(data.getData().getTeam_list());
                adapter.notifyDataSetChanged();

                tv_num.setText(data.getData().getTeam_rec_count());
            }

            @Override
            public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                mRefresh.setRefreshing(false);
                isLoadingMore = false;

                try {
                    if (obj.getJSONObject("data").getJSONArray("team_list").length() > 0) {
                        if (pindex == 1) pageNum = pindex;
                        pageNum++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

}
