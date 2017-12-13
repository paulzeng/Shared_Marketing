package com.ruanmeng.shared_marketing;

import android.content.Intent;
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
import com.ruanmeng.model.NoticeData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeActivity extends BaseActivity {

    @BindView(R.id.lv_notice_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_notice_refresh)
    SwipeRefreshLayout mRefresh;

    private List<NoticeData.NoticeInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);
        init_title("公告");

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void init_title() {
        super.init_title();
        tv_hint.setText("暂无公告信息");
        iv_hint.setImageResource(R.mipmap.not_notice);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<NoticeData.NoticeInfo>(this, R.layout.item_notice_list, list) {
            @Override
            protected void convert(ViewHolder holder, final NoticeData.NoticeInfo info, int position) {

                holder.setText(R.id.tv_item_notice_name, info.getTitle());
                holder.setText(R.id.tv_item_notice_time, info.getCreate_time());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(baseContext, NoticeDetailActivity.class);
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
        mRequest = NoHttp.createStringRequest(HttpIP.noticeList, Const.POST);
        mRequest.add("role_type", getString("user_type"));
        mRequest.add("pindex", pindex);

        getRequest(new CustomHttpListener<NoticeData>(baseContext, true, NoticeData.class) {
            @Override
            public void doWork(NoticeData data, String code) {
                if (pindex == 1) list.clear();

                if (data.getData().size() > 0) {
                    list.addAll(data.getData());
                    int pos = adapter.getItemCount();
                    adapter.notifyItemRangeInserted(pos, list.size());
                }
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
