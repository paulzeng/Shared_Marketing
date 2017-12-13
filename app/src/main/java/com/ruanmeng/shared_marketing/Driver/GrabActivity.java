package com.ruanmeng.shared_marketing.Driver;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
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
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
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

public class GrabActivity extends BaseActivity {

    @BindView(R.id.mv_grab_map)
    MapView mMapView;
    @BindView(R.id.iv_grab_img)
    RoundedImageView iv_img;
    @BindView(R.id.tv_grab_name1)
    TextView tv_name1;
    @BindView(R.id.tv_grab_addr1)
    TextView tv_start;
    @BindView(R.id.tv_grab_addr2)
    TextView tv_end;
    @BindView(R.id.tv_grab_name2)
    TextView tv_name2;
    @BindView(R.id.tv_grab_tel)
    TextView tv_tel;
    @BindView(R.id.tv_grab_num)
    TextView tv_num;

    private CommonData.CommonInfo info;

    private BaiduMap mBaiduMap;
    /*路径规划*/
    private DrivingRouteLine route;
    private RoutePlanSearch rmSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grab);
        ButterKnife.bind(this);
        init_title("订单详情");

        final String sLat = info.getDeparture_lat();
        final String sLng = info.getDeparture_lng();
        final String eLat = info.getDestination_lat();
        final String eLng = info.getDestination_lng();

        LatLng ll = new LatLng(
                TextUtils.isEmpty(sLat) ? Double.parseDouble(eLat) : Double.parseDouble(sLat),
                TextUtils.isEmpty(sLng) ? Double.parseDouble(eLng) : Double.parseDouble(sLng));
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 500);
    }

    @Override
    public void init_title() {
        super.init_title();
        info = (CommonData.CommonInfo) getIntent().getSerializableExtra("info");

        Glide.with(this)
                .load(info.getLogo())
                .placeholder(R.mipmap.personal_a20) // 等待时的图片
                .error(R.mipmap.personal_a20) // 加载失败的图片
                .crossFade()
                .dontAnimate()
                .into(iv_img);

        tv_name1.setText("申请人：" + info.getName());
        tv_start.setText(info.getDeparture_place());
        tv_end.setText(info.getDestination_place());
        tv_name2.setText(info.getName());
        tv_tel.setText(info.getMobile());
        tv_num.setText(info.getNumber_of_person() + "人");

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
        //设置是否显示缩放控件
        mMapView.showZoomControls(false);
        //设置是否显示比例尺
        mMapView.showScaleControl(false);
        //是否隐藏百度Logo
        mMapView.getChildAt(1).setVisibility(View.GONE);

        //路径规划功能：初始化模块，注册事件监听
        //展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制同时展示如何进行节点浏览并弹出泡泡
        rmSearch = RoutePlanSearch.newInstance();
        rmSearch.setOnGetRoutePlanResultListener(new MyGetRoutePlanResultListener());
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.iv_grab_tel:
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
            case R.id.btn_grab_qiang:
                mRequest = NoHttp.createStringRequest(HttpIP.grabOrder, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("order_id", info.getId());

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        EventBus.getDefault().post(new DriverMessageEvent("1"));

                        onBackPressed();
                    }

                    @Override
                    public void onFinally(JSONObject obj, String code, boolean isSucceed) {
                        if (isSucceed && TextUtils.equals("0", code)) {
                            EventBus.getDefault().post(new DriverMessageEvent("1"));

                            onBackPressed();
                        }
                    }
                });
                break;
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
        public void onGetWalkingRouteResult(WalkingRouteResult result) {
        }

        /**
         * 获取公交换乘路径规划结果
         */
        @Override
        public void onGetTransitRouteResult(TransitRouteResult result) {
        }

        /**
         * 获取跨城综合公共交通线路规划结果
         */
        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        }

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
        public void onGetIndoorRouteResult(IndoorRouteResult result) {
        }

        /**
         * 获取骑行路线结规划结果
         */
        @Override
        public void onGetBikingRouteResult(BikingRouteResult result) {
        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
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
                popupText.setPadding(0, 16, 0, 0);
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
