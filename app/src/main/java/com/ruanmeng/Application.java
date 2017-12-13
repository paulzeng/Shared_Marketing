/**
 * created by 小卷毛, 2016/11/15
 * Copyright (c) 2016, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG            #
 * #                                                   #
 */
package com.ruanmeng;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.baidu.mapapi.SDKInitializer;
import com.ruanmeng.service.TimeStampService;
import com.ruanmeng.share.Const;
import com.ruanmeng.shared_marketing.BuildConfig;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import java.util.Timer;

import cn.jpush.android.api.JPushInterface;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-114 16:49
 */
public class Application extends android.app.Application {

    public static long timeStamp = 1487381088L;
    private static Application instance;

    public static synchronized Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 百度地图初始化
        SDKInitializer.initialize(getApplicationContext());

        // NoHttp配置
        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(30 * 1000)   // 设置全局连接超时时间，单位毫秒
                .setReadTimeout(30 * 1000));    // 设置全局服务器响应超时时间，单位毫秒
        Logger.setTag("Shared_Marketing");      // 设置NoHttp打印Log的tag
        Logger.setDebug(BuildConfig.LOG_DEBUG); // 开始NoHttp的调试模式, build.gradle中配置是否打印输出

        // 极光推送
        JPushInterface.setDebugMode(BuildConfig.LOG_DEBUG); //设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		                //初始化 JPush

        // 友盟分享
        PlatformConfig.setWeixin("wxc2d100c58d663a71", "173dd4f60edae5d6be1eaa6a3db46787");
        PlatformConfig.setQQZone("1105925363", "yB1QTRvxY30hqIem");
        UMShareAPI.get(this);
        Config.DEBUG = BuildConfig.LOG_DEBUG;
        Config.isJumptoAppStore = true;

        // 获取当前系统时间戳
        Const.timeStamp = System.currentTimeMillis() / 1000;

        // 绑定服务
        bindService(new Intent(this, TimeStampService.class), new ServiceConnection() {
            // 当与service的连接建立后被调用
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service instanceof TimeStampService.TimeStampBinder) {
                    Timer mTimeStampTimer = ((TimeStampService.TimeStampBinder) service).getTimeStampTimer();
                    if (mTimeStampTimer == null) {
                        ((TimeStampService.TimeStampBinder) service).getService().setTimeStampTimer();
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) { }

        }, BIND_AUTO_CREATE);
    }

}
