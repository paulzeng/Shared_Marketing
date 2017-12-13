package com.ruanmeng.shared_marketing.Partner;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.ruanmeng.adapter.MultiTypeGapDecoration;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.CityData;
import com.ruanmeng.model.HouseMessageEvent;
import com.ruanmeng.model.MainMessageEvent;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.BDLocationUtil;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CityActivity extends BaseActivity {

    @BindView(R.id.tv_city_location)
    TextView tv_location;
    @BindView(R.id.lv_city_list)
    RecyclerView mRecyclerView;

    private List<CityData.CityInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);
        init_title("城市选择");

        CityActivityPermissionsDispatcher.needsPermissionWithCheck(this);
        getData();
    }

    @Override
    public void init_title() {
        super.init_title();
        ivBack.setImageResource(R.mipmap.ico_close);

        gridLayoutManager = new GridLayoutManager(this, 3);

        MultiTypeGapDecoration gapDecoration=new MultiTypeGapDecoration(10);
        gapDecoration.setOffsetTopEnabled(true);
        mRecyclerView.addItemDecoration(gapDecoration);

        adapter = new CommonAdapter<CityData.CityInfo>(this, R.layout.item_one_textview, list) {
            @Override
            protected void convert(ViewHolder holder, final CityData.CityInfo info, int position) {
                holder.setText(R.id.tv_item_city_name, info.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new MainMessageEvent(
                                getString("user_type"),
                                info.getName()));

                        putString("city_id", info.getId());
                        EventBus.getDefault().post(new HouseMessageEvent("楼盘更新"));

                        onBackPressed();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.getOpenCityList, Const.POST);

        getRequest(new CustomHttpListener<CityData>(this, true, CityData.class) {
            @Override
            public void doWork(CityData data, String code) {
                if (data.getData().size() > 0) {
                    list.addAll(data.getData());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        BDLocationUtil.getInstance(this).stopLocation();
        super.onDestroy();
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void needsPermission() {
        BDLocationUtil.getInstance(this).startLocation(new BDLocationUtil.LocationCallback() {
            @Override
            public void doWork(BDLocation location, int code) {
                if (code == 2) {
                    tv_location.setText("当前定位城市：" + location.getCity());
                }
            }
        }, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CityActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationale(final PermissionRequest request) {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.content("需要使用定位功能获取当前位置")
                .title("需要权限")
                .btnText("取消", "确定")
                .btnTextColor(
                        getResources().getColor(R.color.black),
                        getResources().getColor(R.color.blue))
                .showAnim(new BounceTopEnter())
                .show();
        materialDialog.setOnBtnClickL(
                new OnBtnClickL() { //left btn click listener
                    @Override
                    public void onBtnClick() {
                        materialDialog.dismiss();
                        request.cancel();
                    }
                },
                new OnBtnClickL() { //right btn click listener
                    @Override
                    public void onBtnClick() {
                        materialDialog.dismiss();
                        request.proceed();
                    }
                });
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void permissionDenied() {
        showToask("请求权限被拒绝，请重新开启权限");
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void neverAskAgain() {
        showToask("不再询问权限");
    }

}
