package com.ruanmeng.shared_marketing.Partner;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.fragment.MapFirstFragment;
import com.ruanmeng.fragment.MapSecondFragment;
import com.ruanmeng.model.MapMessageEvent;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.BDLocationUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointMapActivity extends BaseActivity {

    @BindView(R.id.mv_appoint_map)
    MapView mMapView;
    @BindView(R.id.ll_appoint_map_center)
    LinearLayout ll_center;

    private BaiduMap mBaiduMap;
    private GeoCoder mSearch;
    private LatLng mCenterLatLng;
    private String mCity, mAddress;

    private MapFirstFragment first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_map);
        ButterKnife.bind(this);
        transparentStatusBar(false);
        init_title();

        first = new MapFirstFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_appoint_map_container, first)
                .commit();

        if (!TextUtils.isEmpty(getString("map_lat", ""))) {
            mCenterLatLng = new LatLng(
                    Double.parseDouble(getString("map_lat")),
                    Double.parseDouble(getString("map_lng")));
            setCenterLocation(mCenterLatLng);

            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mCenterLatLng));
        } else setLocation();
    }

    @Override
    public void init_title() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //设置地图缩放级别，百度地图将地图的级别定义为（3~19）
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(19));
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //设置是否显示室内图, 默认室内图不显示
        mBaiduMap.setIndoorEnable(true);
        //设置是否允许楼块效果
        mBaiduMap.setBuildingsEnabled(false);
        //设置是否允许指南针
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        //设置是否允许旋转手势
        mBaiduMap.getUiSettings().setRotateGesturesEnabled(false);
        //设置是否允许俯视手势
        mBaiduMap.getUiSettings().setOverlookingGesturesEnabled(false);
        //是否隐藏缩放控件
        mMapView.showZoomControls(true);
        //设置控制缩放控件的位置
        mMapView.getChildAt(2).setPadding(0, 0, 20, 40);
        //是否隐藏比例尺
        mMapView.showScaleControl(false);
        //是否隐藏百度Logo
        mMapView.getChildAt(1).setVisibility(View.GONE);
        //设置百度Logo的位置
        // mMapView.getChildAt(1).setPadding(0,0,100,100);

        //地理编码功能：初始化模块，注册事件监听
        //展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new MyGetGeoCoderResultListener());

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            /**
             * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
             *
             * @param status
             *            地图状态改变开始时的地图状态
             */
            @Override
            public void onMapStatusChangeStart(MapStatus status) { }

            /**
             * 地图状态改变结束
             * @param status
             *            地图状态改变结束后的地图状态
             */
            @Override
            public void onMapStatusChangeFinish(MapStatus status) {
                /* 获取中心经纬度 */
                mCenterLatLng = status.target;
                putString("map_lat", String.valueOf(mCenterLatLng.latitude));
                putString("map_lng", String.valueOf(mCenterLatLng.longitude));

                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(mCenterLatLng));
            }

            /**
             * 地图状态变化中
             * @param status
             *            当前地图状态
             */
            @Override
            public void onMapStatusChange(MapStatus status) { }
        });
    }

    public void setCenterLocation(LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(u);
    }

    private void setLocation() {
        BDLocationUtil.getInstance(this).startLocation(new BDLocationUtil.LocationCallback() {
            @Override
            public void doWork(BDLocation location, int code) {
                //地图销毁后不再处理新接收的位置
                if (location == null || mMapView == null) return;

                if (code == 4) {
                    mCity = location.getCity();

                    //移动至中心点
                    mCenterLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    setCenterLocation(mCenterLatLng);
                }
            }
        }, 4);
    }

    private void startTranslationAnimator(final View target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "translationY", 0, -100, 0);
        animator.setDuration(1000);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.push_left_in,
                        R.anim.push_left_out,
                        R.anim.push_right_in,
                        R.anim.push_right_out)
                .add(R.id.fl_appoint_map_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void removeFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_appoint_map_location:
                setLocation();
                break;
            case R.id.ll_fragment_first_addr:
                MapSecondFragment second = new MapSecondFragment();
                Bundle extra = new Bundle();
                extra.putString("city", mCity);
                extra.putParcelable("point", mCenterLatLng);
                second.setArguments(extra);
                addFragment(second);
                break;
            case R.id.tv_fragment_second_cancle:
                removeFragment();
                break;
            case R.id.tv_appoint_map_center:
                if (mAddress == null) return;

                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mAddress != null && mCenterLatLng != null) {
            EventBus.getDefault().post(new MapMessageEvent(
                    mAddress,
                    String.valueOf(mCenterLatLng.latitude),
                    String.valueOf(mCenterLatLng.longitude)));
        }

        super.onBackPressed();
    }

    /**
     * 地理编码监听器
     */
    private class MyGetGeoCoderResultListener implements OnGetGeoCoderResultListener {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {  //地址转坐标
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR)
                showToask("未找到结果");
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) { //反Geo搜索，坐标转地址
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                showToask("未找到结果");
                return;
            }
            if (first != null) {
                mCity = result.getAddressDetail().city;
                first.setCity(mCity);

                String desc = result.getSematicDescription();
                mAddress = desc.contains(",") ? desc.substring(desc.lastIndexOf(",") + 1) : desc;
                first.setAddress(mAddress);
            }

            startTranslationAnimator(ll_center);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BDLocationUtil.getInstance(this).stopLocation();
        //关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mSearch.destroy();
    }

}
