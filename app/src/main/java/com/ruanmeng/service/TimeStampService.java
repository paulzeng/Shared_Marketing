package com.ruanmeng.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.dialog.nohttp.CallServer;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.utils.MD5Util;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-03-20 16:27
 */

public class TimeStampService extends Service {

    private Timer mTimeStampTimer;
    private TimerTask mTimeStampTimerTask;
    private TimerTask mTimerTask;

    private Request<String> mRequest;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getTimestamp();
        }
    };

    private final IBinder binder = new TimeStampBinder(); //绑定器

    public class TimeStampBinder extends Binder {
        public TimeStampService getService() {
            return TimeStampService.this;
        }

        public Timer getTimeStampTimer() {
            return mTimeStampTimer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setTimeStampTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setTimeStampTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    public void setTimeStampTimer() {
        if (mTimeStampTimer == null) {
            mTimeStampTimer = new Timer();
            mTimeStampTimerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Const.timeStamp ++;
                    // Logger.i(Const.timeStamp);
                }
            };

            mTimeStampTimer.schedule(mTimeStampTimerTask, 0, 3 * 60 * 1000);
            mTimeStampTimer.schedule(mTimerTask, 1000, 1000);
        }
    }

    /**
     * 获取系统时间戳
     */
    public void getTimestamp() {
        mRequest = NoHttp.createStringRequest(HttpIP.getSystemTimestamp, Const.POST);
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));
        // 添加到请求队列
        CallServer.getRequestInstance().add(this, mRequest,
                new CustomHttpListener<JSONObject>(this, false, JSONObject.class) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    String timestamp = data.getJSONObject("data").getString("timestamp");
                    Const.timeStamp = Long.parseLong(timestamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    @Override
    public void onDestroy() {
        if (mRequest != null) mRequest.cancel();
        if (mTimeStampTimerTask != null) {
            mTimeStampTimerTask.cancel();
            mTimeStampTimerTask = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimeStampTimer != null) {
            mTimeStampTimer.cancel();
            mTimeStampTimer = null;
        }
        super.onDestroy();
    }

}
