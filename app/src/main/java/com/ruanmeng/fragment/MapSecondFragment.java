package com.ruanmeng.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.ruanmeng.shared_marketing.Partner.AppointMapActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapSecondFragment extends Fragment {


    @BindView(R.id.tv_fragment_second_city)
    TextView tv_city;
    @BindView(R.id.et_fragment_second_city)
    EditText et_city;
    @BindView(R.id.lv_fragment_second_list)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private PoiSearch mPoiSearch;

    private CommonAdapter<PoiInfo> adapter;
    private List<PoiInfo> list = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_map_second, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        getNearByData();
    }

    private void getNearByData() {
        LatLng latLng = getArguments().getParcelable("point");

        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(getArguments().getString("city"))
                .sortType(PoiSortType.distance_from_near_to_far)
                .location(latLng)
                .radius(1000)
                .pageCapacity(100)
                .pageNum(0);
        mPoiSearch.searchNearby(nearbySearchOption);
    }

    private void init() {
        tv_city.setText(getArguments().getString("city"));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<PoiInfo>(getActivity(), R.layout.item_map_list, list) {
            @Override
            protected void convert(ViewHolder holder, final PoiInfo info, int position) {
                holder.setText(R.id.tv_item_map_name, info.name);
                holder.setText(R.id.tv_item_map_addr, info.address);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AppointMapActivity) getActivity()).setCenterLocation(info.location);
                        ((AppointMapActivity) getActivity()).removeFragment();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);

        // POI检索实例
        mPoiSearch = PoiSearch.newInstance();
        // POI检索监听者
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        et_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mPoiSearch.searchInCity((new PoiCitySearchOption())
                            .city(getArguments().getString("city"))
                            .keyword(s.toString())
                            .pageCapacity(100)
                            .pageNum(0));
                } else {
                    getNearByData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        /**
         * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
         */
        @Override
        public void onGetPoiResult(PoiResult result) {
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                CommonUtil.showToask(getActivity(), "未找到相关结果");
                return;
            }

            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                list.clear();
                list.addAll(result.getAllPoi());
                adapter.notifyDataSetChanged();
            }

            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                CommonUtil.showToask(getActivity(), "在当前城市未找到相关结果");
            }
        }

        /**
         * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
         */
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.layout_title)
    public void onViewClicked() {
    }
}
