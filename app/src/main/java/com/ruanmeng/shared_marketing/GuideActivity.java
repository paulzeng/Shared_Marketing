package com.ruanmeng.shared_marketing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.shared_marketing.Adviser.MainAdviserActivity;
import com.ruanmeng.shared_marketing.Case.MainCaseActivity;
import com.ruanmeng.shared_marketing.Channel.MainChannelActivity;
import com.ruanmeng.shared_marketing.Distribution.MainDistributionActivity;
import com.ruanmeng.shared_marketing.Driver.MainDriverActivity;
import com.ruanmeng.shared_marketing.Partner.MainActivity;
import com.ruanmeng.shared_marketing.Resident.MainResidentActivity;
import com.ruanmeng.shared_marketing.Specialist.MainSpecialistActivity;
import com.ruanmeng.shared_marketing.Unit.MainUnitActivity;
import com.ruanmeng.utils.PreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class GuideActivity extends BaseActivity {

    @BindView(R.id.iv_guide_bg)
    ImageView iv_img;

    private boolean isReady;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isReady) {
                if (PreferencesUtils.getBoolean(baseContext, "isLogin", false)) {
                    switch (PreferencesUtils.getString(baseContext, "user_type")) {
                        case "1": // 1：案场经理
                            startActivity(MainCaseActivity.class);
                            break;
                        case "2": // 2：置业顾问
                            startActivity(MainAdviserActivity.class);
                            break;
                        case "3": // 3：驻场
                            startActivity(MainResidentActivity.class);
                            break;
                        case "4": // 4：员工合伙人
                        case "5": // 5：业主合伙人
                        case "6": // 6：社会合伙人
                            startActivity(MainActivity.class);
                            break;
                        case "7": // 7：合作单位合伙人
                            startActivity(MainUnitActivity.class);
                            break;
                        case "8": // 8：渠道经理
                            startActivity(MainChannelActivity.class);
                            break;
                        case "9": // 9：分销专员
                            startActivity(MainDistributionActivity.class);
                            break;
                        case "10": // 10：渠道专员
                            startActivity(MainSpecialistActivity.class);
                            break;
                        case "11": // 11：司机
                            startActivity(MainDriverActivity.class);
                            break;
                    }
                } else {
                    startActivity(LoginActivity.class);
                }

                onBackPressed();
            } else {
                isReady = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        transparentStatusBar(false);

        GuideActivityPermissionsDispatcher.needPermissionWithCheck(this);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 2000);
    }

    @NeedsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void needPermission() {
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GuideActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void permissionDenied() {
        showToask("请求权限被拒绝，请重新开启权限");
    }

    @OnNeverAskAgain(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void neverAskAgain() {
        showToask("不再询问权限");
    }
}
