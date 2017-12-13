package com.ruanmeng.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dialog.nohttp.CallServer;
import com.ruanmeng.model.GrabData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GrabFragment extends Fragment {

    @BindView(R.id.lv_fragment_grab_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.tv_fragment_grab_total)
    TextView tv_total;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_fragment_grab_refresh)
    SwipeRefreshLayout mRefresh;

    private Request<String> mRequest;
    private List<GrabData.CommonInfo> list = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter<GrabData.CommonInfo> adapter;
    private boolean isLoadingMore;

    private int pageNum = 1, mTotal;

    //调用这个方法切换时不会释放掉Fragment
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grab, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    public void setRefreshList() {
        mRefresh.setRefreshing(true);

        list.clear();
        adapter.notifyDataSetChanged();

        getData(pageNum = 1);
    }

    private void init() {
        tv_hint.setText("暂无抢单信息");
        iv_hint.setImageResource(R.mipmap.not_search);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<GrabData.CommonInfo>(getActivity(), R.layout.item_grab_list, list) {
            @Override
            protected void convert(ViewHolder holder, final GrabData.CommonInfo info, int position) {
                holder.setText(R.id.tv_item_grab_name, info.getName());
                holder.setText(R.id.tv_item_grab_phone, info.getMobile());
                holder.setText(R.id.tv_item_grab_project, info.getProj_name());
                holder.setText(R.id.tv_item_grab_recommend, info.getReal_name());

                holder.setOnClickListener(R.id.tv_item_grab_qiang, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*DialogHelper.showDialog(
                                mContext,
                                "抢单",
                                "确定要抢单吗？",
                                "取消",
                                "抢单", new DialogHelper.HintCallBack() {
                                    @Override
                                    public void doWork() {
                                        mRequest = NoHttp.createStringRequest(HttpIP.grabCustomer, Const.POST);
                                        mRequest.add("user_id", PreferencesUtils.getString(mContext, "user_id"));
                                        mRequest.add("customer_id", info.getId());
                                        mRequest.add("token", MD5Util.md5(Const.TIMESTAMP));
                                        mRequest.add("time", Const.TIMESTAMP);

                                        // 添加到请求队列
                                        CallServer.getRequestInstance().add(getActivity(), mRequest,
                                                new CustomHttpListener<JSONObject>(getActivity(), false, null) {
                                                    @Override
                                                    public void doWork(JSONObject data, String code) {
                                                        int position = list.indexOf(info);
                                                        list.remove(position);
                                                        adapter.notifyItemRemoved(position);
                                                        ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);

                                                        ((MainAdviserActivity) getActivity()).jumpToMain();
                                                    }
                                                }, true);
                                    }
                                });*/

                        mRequest = NoHttp.createStringRequest(HttpIP.grabCustomer, Const.POST);
                        mRequest.add("user_id", PreferencesUtils.getString(mContext, "user_id"));
                        mRequest.add("customer_id", info.getId());
                        mRequest.add("token", MD5Util.md5(Const.timeStamp));
                        mRequest.add("time", String.valueOf(Const.timeStamp));

                        // 添加到请求队列
                        CallServer.getRequestInstance().add(getActivity(), mRequest,
                                new CustomHttpListener<JSONObject>(getActivity(), false, null) {
                                    @Override
                                    public void doWork(JSONObject data, String code) {
                                        int position = list.indexOf(info);
                                        mTotal -= 1;
                                        tv_total.setText("共" + mTotal + "个客户");
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        ll_hint.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);

                                        // ((MainAdviserActivity) getActivity()).jumpToMain();
                                    }

                                    @Override
                                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                                        try {
                                            if (TextUtils.equals("0", code)
                                                    && TextUtils.equals("该单已被其他置业顾问抢单成功！", obj.getString("msg"))) {
                                                setRefreshList();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, true);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                adapter.notifyDataSetChanged();

                getData(pageNum = 1);
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

    public void getData(final int pindex) {
        mRequest = NoHttp.createStringRequest(HttpIP.Customerlist_new, Const.POST);
        mRequest.add("pindex", pindex);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<GrabData>(getActivity(), true, GrabData.class) {
                    @Override
                    public void doWork(GrabData data, String code) {
                        if (pindex == 1) list.clear();

                        list.addAll(data.getData().getData());
                        adapter.notifyDataSetChanged();

                        mTotal = data.getData().getNum();
                        tv_total.setText("共" + mTotal + "个客户");
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
