package com.ruanmeng.fragment;

import android.content.Intent;
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
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.dialog.nohttp.CallServer;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.RollPagerView;
import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.adapter.LoopAdapter;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.model.HouseData;
import com.ruanmeng.model.NoticeData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.NoticeDetailActivity;
import com.ruanmeng.shared_marketing.Partner.HouseDetailActivity;
import com.ruanmeng.shared_marketing.Partner.MainActivity;
import com.ruanmeng.shared_marketing.Partner.RecommendActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.ruanmeng.view.AlwaysMarqueeTextView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.tv_fragment_home_title)
    TextView tv_title;
    @BindView(R.id.tv_fragment_home_location)
    TextView tv_location;
    @BindView(R.id.iv_fragment_home_msg)
    ImageView iv_msg;
    @BindView(R.id.tv_fragment_home_gonggao)
    AlwaysMarqueeTextView tv_notice;
    @BindView(R.id.el_fragment_home_expand)
    ExpandableLayout expand;
    @BindView(R.id.lv_fragment_home_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_fragment_home_refresh)
    SwipeRefreshLayout mRefresh;

    private RollPagerView mBannerRoll;

    private LinearLayoutManager linearLayoutManager;
    private HeaderAndFooterRecyclerViewAdapter headerAndFooterAdapter;
    private int pageNum = 1;
    private boolean isLoadingMore;
    private LoopAdapter mLoopAdapter;
    private List<HouseData.HouseInfo> list = new ArrayList<>();
    private List<CommonData.CommonInfo> list_banner = new ArrayList<>();
    private List<String> imgs = new ArrayList<>();

    private Request<String> mRequest;
    private String id_notice;

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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        getNotice();
        getBanner();

        mRefresh.setRefreshing(true);
        getData(pageNum);
    }

    @Override
    public void onStart() {
        super.onStart();

        setLocation(PreferencesUtils.getString(getActivity(), "city_name"));
    }

    private void init() {
        tv_title.setText("首页");
        setNoticeIcon(false);

        View view = View.inflate(getActivity(), R.layout.header_home, null);
        mBannerRoll = (RollPagerView) view.findViewById(R.id.rp_fragment_home_banner);
        SuperTextView tv_tui = (SuperTextView) view.findViewById(R.id.stv_fragment_home_tui);
        SuperTextView tv_jing = (SuperTextView) view.findViewById(R.id.stv_fragment_home_jing);
        tv_tui.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener(){
            @Override
            public void onSuperTextViewClick() {
                Intent intent = new Intent(getActivity(), RecommendActivity.class);
                startActivity(intent);
            }
        });
        tv_jing.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener(){
            @Override
            public void onSuperTextViewClick() {
                ((MainActivity) getActivity()).jumpToHouse();
            }
        });
        mBannerRoll.setAdapter(mLoopAdapter = new LoopAdapter(getActivity(), mBannerRoll));
        // mLoopViewPager.setHintView(new IconHintView(this,R.drawable.point_focus,R.drawable.point_normal));
        // mRollViewPager.setHintView(new ColorPointHintView(this, Color.YELLOW,Color.WHITE));
        // mRollViewPager.setHintView(new TextHintView(this));
        // mRollViewPager.setHintView(null);
        mBannerRoll.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CommonData.CommonInfo item = list_banner.get(position);
                if (!TextUtils.isEmpty(item.getLink())) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("type", "9");
                    intent.putExtra("url", item.getLink());
                    startActivity(intent);
                }
            }
        });

        mRefresh.setColorSchemeResources(R.color.colorAccent);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CommonAdapter mAdapter = new CommonAdapter<HouseData.HouseInfo>(getActivity(), R.layout.item_house_list, list) {
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

        headerAndFooterAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        headerAndFooterAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(headerAndFooterAdapter);

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBanner();
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

    public void setLocation(String city_name) {
        tv_location.setText(city_name);
    }

    public void setNoticeIcon(boolean isShown) {
        iv_msg.setImageResource(isShown ? R.mipmap.ico_ann_b : R.mipmap.ico_ann);
    }

    private void getBanner() {
        mRequest = NoHttp.createStringRequest(HttpIP.slider, Const.POST);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<CommonData>(getActivity(), true, CommonData.class) {
                    @Override
                    public void doWork(CommonData data, String code) {
                        list_banner.clear();
                        imgs.clear();
                        list_banner.addAll(data.getData());

                        if (list_banner.size() > 0) {
                            for (CommonData.CommonInfo item :list_banner) {
                                imgs.add(item.getImage());
                            }
                            mLoopAdapter.setImgs(imgs);

                            if (imgs.size() <= 1) {
                                mBannerRoll.pause();
                                mBannerRoll.setHintViewVisibility(false);
                            }
                            else {
                                mBannerRoll.resume();
                                mBannerRoll.setHintViewVisibility(true);
                            }
                        }
                    }

                }, false);
    }

    private void getNotice() {
        mRequest = NoHttp.createStringRequest(HttpIP.noticeList, Const.POST);
        mRequest.add("role_type", PreferencesUtils.getString(getActivity(), "user_type"));
        mRequest.add("pindex", 1);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<NoticeData>(getActivity(), true, NoticeData.class) {
                    @Override
                    public void doWork(NoticeData data, String code) {

                        if (data.getData().size() > 0) {
                            id_notice = data.getData().get(0).getId();
                            tv_notice.setText("公告：" + data.getData().get(0).getTitle());
                        } else
                            expand.collapse();
                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        if (!TextUtils.equals("1", code)) expand.collapse();
                    }
                }, false);
    }

    private void getData(final int pindex) {
        Request<String> mRequest = NoHttp.createStringRequest(HttpIP.buildingList, Const.POST);
        mRequest.add("pindex", pindex);
        mRequest.add("city_id", PreferencesUtils.getString(getActivity(), "city_id"));
        mRequest.add("type", "1");
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<HouseData>(getActivity(), true, HouseData.class) {
                    @Override
                    public void doWork(HouseData data, String code) {
                        if (pindex == 1) list.clear();

                        list.addAll(data.getData());
                        headerAndFooterAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        mRefresh.setRefreshing(false);
                        isLoadingMore = false;

                        if (TextUtils.equals("1", code)) {
                            if (pindex == 1) pageNum = pindex;
                            pageNum++;
                        }
                    }
                }, false);
    }

    public void updateHouseList() {
        mRefresh.setRefreshing(true);
        if (list.size() > 0) {
            list.clear();
            headerAndFooterAdapter.notifyDataSetChanged();
        }

        // getBanner();

        pageNum = 1;
        getData(pageNum);
    }

    @OnClick({
            R.id.tv_fragment_home_gonggao,
            R.id.iv_fragment_home_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_fragment_home_gonggao:
                if (id_notice != null) {
                    Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
                    intent.putExtra("id", id_notice);
                    startActivity(intent);
                }
                break;
            case R.id.iv_fragment_home_close:
                expand.collapse();
                break;
        }
    }
}
