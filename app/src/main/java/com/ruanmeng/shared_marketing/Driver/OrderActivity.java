package com.ruanmeng.shared_marketing.Driver;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.model.DriverMessageEvent;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.BDLocationUtil;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity {

    @BindView(R.id.mv_order_map)
    MapView mMapView;
    @BindView(R.id.iv_order_daohang)
    ImageView iv_nav;
    @BindView(R.id.tv_order_done)
    TextView tv_done;

    private BaiduMap mBaiduMap;
    private LatLng mCenterLatLng;
    /*路径规划*/
    private DrivingRouteLine route;
    private RoutePlanSearch rmSearch;

    private CommonData.CommonInfo info;
    private String mStatus;

    private OrderFirstFragment first;
    private OrderSecondFragment second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        init_title("订单详情");

        if (!TextUtils.isEmpty(getString("map_lat", ""))) {
            mCenterLatLng = new LatLng(
                    Double.parseDouble(getString("map_lat")),
                    Double.parseDouble(getString("map_lng")));
            setCenterLocation(mCenterLatLng);
        } else setLocation();

        first = new OrderFirstFragment();
        second = new OrderSecondFragment();
        Bundle extra = new Bundle();
        extra.putSerializable("info", info);

        first.setArguments(extra);
        second.setArguments(extra);

        if (TextUtils.equals("3", mStatus)) {
            tv_done.setClickable(false);
            tv_done.setBackgroundResource(R.drawable.rec_ova_bg_999999);
            tv_done.setText("订单已完成");

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_order_container, second)
                    .commit();

            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRoutePlan();
                }
            }, 500);
        } else {
            iv_nav.setVisibility(View.GONE);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_order_container, first)
                    .commit();
        }
    }

    @Override
    public void init_title() {
        super.init_title();
        info = (CommonData.CommonInfo) getIntent().getSerializableExtra("info");
        mStatus = info.getO_status();

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

        //路径规划功能：初始化模块，注册事件监听
        //展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制同时展示如何进行节点浏览并弹出泡泡
        rmSearch = RoutePlanSearch.newInstance();
        rmSearch.setOnGetRoutePlanResultListener(new MyGetRoutePlanResultListener());
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

                if (code == 6) {
                    //移动至中心点
                    mCenterLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    putString("map_lat", String.valueOf(mCenterLatLng.latitude));
                    putString("map_lng", String.valueOf(mCenterLatLng.longitude));

                    setCenterLocation(mCenterLatLng);
                }
            }
        }, 6);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_order_location:
                setLocation();
                break;
            case R.id.iv_order_tel1:
            case R.id.iv_order_tel2:
                if (TextUtils.isEmpty(info.getMobile())) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        info.getName() + "电话：" + info.getMobile(),
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
                break;
            case R.id.iv_order_nav:
                iv_nav.setVisibility(View.VISIBLE);

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.push_left_in,
                                R.anim.push_left_out,
                                R.anim.push_right_in,
                                R.anim.push_right_out)
                        .hide(first)
                        .add(R.id.fl_order_container, second)
                        .commit();

                startRoutePlan();
                break;
            case R.id.iv_order_daohang:
                startNavi();
                // startRoutePlanDriving();
                break;
            case R.id.tv_order_done:
                mRequest = NoHttp.createStringRequest(HttpIP.finishOrder, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("order_id", info.getId());

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        EventBus.getDefault().post(new DriverMessageEvent("2"));

                        onBackPressed();
                    }
                });
                break;
        }
    }

    /**
     * 启动百度地图导航(Native)
     */
    public void startNavi() {
        String sLat = info.getDeparture_lat();
        String sLng = info.getDeparture_lng();
        String eLat = info.getDestination_lat();
        String eLng = info.getDestination_lng();

        LatLng pt1 = new LatLng(Double.parseDouble(sLat), Double.parseDouble(sLng));
        LatLng pt2 = new LatLng(Double.parseDouble(eLat), Double.parseDouble(eLng));

        // 构建导航参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(pt1).endPoint(pt2)
                .startName(info.getDeparture_place()).endName(info.getDestination_place());

        try {
            BaiduMapNavigation.openBaiduMapNavi(para, this);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动百度地图驾车路线规划
     */
    public void startRoutePlanDriving() {
        String sLat = info.getDeparture_lat();
        String sLng = info.getDeparture_lng();
        String eLat = info.getDestination_lat();
        String eLng = info.getDestination_lng();

        LatLng pt1 = new LatLng(Double.parseDouble(sLat), Double.parseDouble(sLng));
        LatLng pt2 = new LatLng(Double.parseDouble(eLat), Double.parseDouble(eLng));

        // 构建route搜索参数
        RouteParaOption para = new RouteParaOption()
                .startPoint(pt1)
                .startName(info.getDeparture_place())
                .endPoint(pt2)
                .endName(info.getDestination_place());

        try {
            BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRoutePlan() {
        String sLat = info.getDeparture_lat();
        String sLng = info.getDeparture_lng();
        String eLat = info.getDestination_lat();
        String eLng = info.getDestination_lng();

        if (!TextUtils.isEmpty(sLat)
                && !TextUtils.isEmpty(sLng)
                && !TextUtils.isEmpty(eLat)
                && !TextUtils.isEmpty(eLng)) {
            //设置起点终点信息，对于tranistSearch来说，城市名无意义(坐标点或地址)
            PlanNode stNode = PlanNode.withLocation(new LatLng(
                    Double.parseDouble(sLat),
                    Double.parseDouble(sLng)));
            PlanNode enNode = PlanNode.withLocation(new LatLng(
                    Double.parseDouble(eLat),
                    Double.parseDouble(eLng)));
            rmSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
        }
    }

    /**
     * 路径规划监听器
     */
    private class MyGetRoutePlanResultListener implements OnGetRoutePlanResultListener {

        /**
         * 获取步行线路规划结果
         */
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult result) { }

        /**
         * 获取公交换乘路径规划结果
         */
        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) { }

        /**
         * 获取跨城综合公共交通线路规划结果
         */
        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) { }

        /**
         * 获取驾车线路规划结果
         */
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                showToask("抱歉，未找到结果");
                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                // result.getSuggestAddrInfo()
                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result.getRouteLines().size() > 0) {
                    route = result.getRouteLines().get(0);
                    MyDrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(result.getRouteLines().get(0));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }
        }

        /**
         * 获取室内路线规划结果
         */
        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult result) { }

        /**
         * 获取骑行路线结规划结果
         */
        @Override
        public void onGetBikingRouteResult(BikingRouteResult result) { }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_qi);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_zhong);
        }

        @Override
        public int getLineColor() {
            return Color.parseColor("#45CC6A");
        }

        @Override
        public boolean onRouteNodeClick(int i) {
            if (route.getAllStep() != null && route.getAllStep().get(i) != null) {
                DrivingRouteLine.DrivingStep step = route.getAllStep().get(i);
                LatLng nodeLocation = step.getEntrance().getLocation();
                String nodeTitle = step.getExitInstructions();

                // 移动节点至中心
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
                // show popup
                TextView popupText = new TextView(baseContext);
                popupText.setBackgroundResource(R.mipmap.location_bg);
                popupText.setTextAppearance(baseContext, R.style.Font13_black);
                popupText.setGravity(Gravity.CENTER_HORIZONTAL);
                popupText.setPadding(10, 16, 10, 0);
                popupText.setText(nodeTitle);
                mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
            }

            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //退出时销毁定位
        BDLocationUtil.getInstance(this).stopLocation();
        //关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        rmSearch.destroy();
    }
}
