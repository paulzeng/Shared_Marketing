package com.ruanmeng.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.bumptech.glide.Glide;
import com.dialog.nohttp.CallServer;
import com.ruanmeng.model.CityData;
import com.ruanmeng.model.HouseData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Partner.HouseDetailActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.AnimationHelper;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PopupWindowUtils;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HouseFragment extends Fragment {

    @BindView(R.id.tv_fragment_team_title)
    TextView tv_title;
    @BindView(R.id.iv_fragment_team_search)
    ImageView iv_search;
    @BindView(R.id.tv_fragment_house_qu)
    TextView tv_qu;
    @BindView(R.id.iv_fragment_house_qu)
    ImageView iv_qu;
    @BindView(R.id.tv_fragment_house_total)
    TextView tv_total;
    @BindView(R.id.iv_fragment_house_total)
    ImageView iv_total;
    @BindView(R.id.tv_fragment_house_hu)
    TextView tv_hu;
    @BindView(R.id.iv_fragment_house_hu)
    ImageView iv_hu;
    @BindView(R.id.tv_fragment_house_ye)
    TextView tv_ye;
    @BindView(R.id.iv_fragment_house_ye)
    ImageView iv_ye;
    @BindView(R.id.lv_fragment_house_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_empty_hint)
    ImageView iv_hint;
    @BindView(R.id.tv_empty_hint)
    TextView tv_hint;
    @BindView(R.id.ll_empty_hint)
    LinearLayout ll_hint;
    @BindView(R.id.rl_fragment_house_refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.v_fragment_house_divider)
    View divider;

    private int pos_qu,
            pos_total,
            pos_hu,
            pos_ye;

    private String name_qu = "",
            name_total = "",
            name_hu = "",
            name_ye = "";

    private List<String> list_qu = new ArrayList<>();
    private List<HouseData.HouseInfo> list = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private CommonAdapter<HouseData.HouseInfo> adapter;
    private int pageNum = 1;
    private boolean isLoadingMore;

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
        View view = inflater.inflate(R.layout.fragment_house, container, false);
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

    private void init() {
        tv_title.setText("楼盘信息");
        tv_hint.setText("暂无楼盘信息");
        iv_hint.setImageResource(R.mipmap.not_house);
        iv_search.setVisibility(View.GONE);

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<HouseData.HouseInfo>(getActivity(), R.layout.item_house_list, list) {
            @Override
            protected void convert(ViewHolder holder, final HouseData.HouseInfo info, int position) {
                ImageView iv_img = holder.getView(R.id.iv_item_house_img);
                holder.setText(R.id.tv_item_house_name, info.getProj_name());
                holder.setText(R.id.tv_item_house_price, info.getHouse_price() + "元/㎡");
                holder.setText(R.id.tv_item_house_addr, info.getDistrict_name());
                holder.setText(R.id.tv_item_house_percent, info.getBasic_commission_rate() + "%/套");
                holder.setText(
                        R.id.tv_item_house_type,
                        info.getYetai_name() + "  " + info.getArea_range() + "  |  " + info.getHouse_type_name());

                Glide.with(mContext)
                        .load(info.getImages())
                        .placeholder(R.mipmap.not_2) // 等待时的图片
                        .error(R.mipmap.not_2) // 加载失败的图片
                        .crossFade()
                        .into(iv_img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, HouseDetailActivity.class);
                        intent.putExtra("id", info.getId());
                        intent.putExtra("title", info.getProj_name());
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

    public void getData(final int pindex) {
        Request<String> mRequest = NoHttp.createStringRequest(HttpIP.buildingList, Const.POST);
        mRequest.add("zone", name_qu);
        mRequest.add("price", name_total);
        mRequest.add("house_type", name_hu);
        mRequest.add("yetai", name_ye);
        mRequest.add("pindex", pindex);
        mRequest.add("city_id", PreferencesUtils.getString(getActivity(), "city_id"));
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<HouseData>(getActivity(), true, HouseData.class) {
                    @Override
                    public void doWork(HouseData data, String code) {
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tv_qu.setTextColor(getResources().getColor(R.color.gray));
                    iv_qu.setImageResource(R.mipmap.arr_1);
                    break;
                case 2:
                    tv_total.setTextColor(getResources().getColor(R.color.gray));
                    iv_total.setImageResource(R.mipmap.arr_1);
                    break;
                case 3:
                    tv_hu.setTextColor(getResources().getColor(R.color.gray));
                    iv_hu.setImageResource(R.mipmap.arr_1);
                    break;
                case 4:
                    tv_ye.setTextColor(getResources().getColor(R.color.gray));
                    iv_ye.setImageResource(R.mipmap.arr_1);
                    break;
            }
        }
    };

    public void updateHouseList() {
        mRefresh.setRefreshing(true);
        if (list.size() > 0) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        pageNum = 1;
        getData(pageNum);
    }

    @OnClick({ R.id.ll_fragment_house_qu,
            R.id.ll_fragment_house_total,
            R.id.ll_fragment_house_hu,
            R.id.ll_fragment_house_ye })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_fragment_house_qu:
                if (list_qu.size() > 0) {
                    tv_qu.setTextColor(getResources().getColor(R.color.colorAccent));
                    iv_qu.setImageResource(R.mipmap.arr_3);
                    AnimationHelper.startRotateAnimator(iv_qu, 0f, 180f);

                    PopupWindowUtils.showDatePopWindow(getActivity(), divider, pos_qu, list_qu, new PopupWindowUtils.PopupWindowCallBack() {
                        @Override
                        public void doWork(int position, String name) {
                            pos_qu = position;
                            if (position > 0) {
                                name_qu = name;
                                tv_qu.setText(name);
                            } else {
                                name_qu = "";
                                tv_qu.setText("区域");
                            }

                            updateHouseList();
                        }

                        @Override
                        public void onDismiss() {
                            AnimationHelper.startRotateAnimator(iv_qu, 180f, 0f);
                            handler.sendEmptyMessageDelayed(1, 300);
                        }
                    });
                } else {
                    Request<String> mRequest = NoHttp.createStringRequest(HttpIP.getDistrictList, Const.POST);
                    mRequest.add("city_id", PreferencesUtils.getString(getActivity(), "city_id"));
                    mRequest.add("token", MD5Util.md5(Const.timeStamp));
                    mRequest.add("time", String.valueOf(Const.timeStamp));

                    CallServer.getRequestInstance().add(getActivity(), mRequest,
                            new CustomHttpListener<CityData>(getActivity(), true, CityData.class) {
                                @Override
                                public void doWork(CityData data, String code) {
                                    list_qu.add("不限");

                                    if (data.getData().size() > 0) {
                                        for (CityData.CityInfo item : data.getData()) {
                                            list_qu.add(item.getName());
                                        }
                                    }

                                    tv_qu.setTextColor(getResources().getColor(R.color.colorAccent));
                                    iv_qu.setImageResource(R.mipmap.arr_3);
                                    AnimationHelper.startRotateAnimator(iv_qu, 0f, 180f);

                                    PopupWindowUtils.showDatePopWindow(getActivity(), divider, pos_qu, list_qu, new PopupWindowUtils.PopupWindowCallBack() {
                                        @Override
                                        public void doWork(int position, String name) {
                                            pos_qu = position;
                                            if (position > 0) {
                                                name_qu = name;
                                                tv_qu.setText(name);
                                            } else {
                                                name_qu = "";
                                                tv_qu.setText("区域");
                                            }

                                            updateHouseList();
                                        }

                                        @Override
                                        public void onDismiss() {
                                            AnimationHelper.startRotateAnimator(iv_qu, 180f, 0f);
                                            handler.sendEmptyMessageDelayed(1, 300);
                                        }
                                    });
                                }
                            }, true);
                }
                break;
            case R.id.ll_fragment_house_total:
                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_total_price");
                    final List<String> list_total = new ArrayList<>();
                    list_total.add("不限");

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_total.add(object.getString("value"));
                    }

                    tv_total.setTextColor(getResources().getColor(R.color.colorAccent));
                    iv_total.setImageResource(R.mipmap.arr_3);
                    AnimationHelper.startRotateAnimator(iv_total, 0f, 180f);

                    PopupWindowUtils.showDatePopWindow(getActivity(), divider, pos_total, list_total, new PopupWindowUtils.PopupWindowCallBack() {
                        @Override
                        public void doWork(int position, String name) {
                            pos_total = position;
                            if (position > 0) {
                                name_total = name;
                                tv_total.setText(name);
                            } else {
                                name_total = "";
                                tv_total.setText("总价");
                            }

                            updateHouseList();
                        }

                        @Override
                        public void onDismiss() {
                            AnimationHelper.startRotateAnimator(iv_total, 180f, 0f);
                            handler.sendEmptyMessageDelayed(2, 300);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_fragment_house_hu:
                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_house_type");
                    final List<String> list_hu = new ArrayList<>();
                    list_hu.add("不限");

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_hu.add(object.getString("value"));
                    }

                    tv_hu.setTextColor(getResources().getColor(R.color.colorAccent));
                    iv_hu.setImageResource(R.mipmap.arr_3);
                    AnimationHelper.startRotateAnimator(iv_hu, 0f, 180f);

                    PopupWindowUtils.showDatePopWindow(getActivity(), divider, pos_hu, list_hu, new PopupWindowUtils.PopupWindowCallBack() {
                        @Override
                        public void doWork(int position, String name) {
                            pos_hu = position;
                            if (position > 0) {
                                name_hu = name;
                                tv_hu.setText(name);
                            } else {
                                name_hu = "";
                                tv_hu.setText("户型");
                            }

                            updateHouseList();
                        }

                        @Override
                        public void onDismiss() {
                            AnimationHelper.startRotateAnimator(iv_hu, 180f, 0f);
                            handler.sendEmptyMessageDelayed(3, 300);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_fragment_house_ye:
                try {
                    JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_yetai");
                    final List<String> list_ye = new ArrayList<>();
                    list_ye.add("不限");

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list_ye.add(object.getString("value"));
                    }

                    tv_ye.setTextColor(getResources().getColor(R.color.colorAccent));
                    iv_ye.setImageResource(R.mipmap.arr_3);
                    AnimationHelper.startRotateAnimator(iv_ye, 0f, 180f);

                    PopupWindowUtils.showDatePopWindow(getActivity(), divider, pos_ye, list_ye, new PopupWindowUtils.PopupWindowCallBack() {
                        @Override
                        public void doWork(int position, String name) {
                            pos_ye = position;
                            if (position > 0) {
                                name_ye = name;
                                tv_ye.setText(name);
                            } else {
                                name_ye = "";
                                tv_ye.setText("业态");
                            }

                            updateHouseList();
                        }

                        @Override
                        public void onDismiss() {
                            AnimationHelper.startRotateAnimator(iv_ye, 180f, 0f);
                            handler.sendEmptyMessageDelayed(4, 300);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
