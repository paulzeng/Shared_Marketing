package com.ruanmeng.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.ruanmeng.nohttp.CallServer;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.PreferencesUtils;
import com.ruanmeng.utils.Tools;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageReadWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * <service
 *   android:name="com.ruanmeng.receiver.UpdateService"
 *   android:process=":update" />
 */
public class UpdateService extends Service {

    protected NotificationCompat.Builder builder;
    protected NotificationManager manager;
    protected long fileLength;
    protected long length;
    protected DeleteReceiver deleteReceiver;
    protected boolean interrupted;

    protected String versionCode;

    /**
     * 下载请求对象.
     */
    public DownloadRequest mDownloadRequest;

    protected static final int TYPE_ID = 2;
    protected static final int TYPE_FINISHED = 0;
    protected static final int TYPE_DOWNLOADING = 1;
    protected static final String TYPE_ACTION = "com.ruanmeng.update.DeleteUpdate";
    protected static final String TYPE_DOWNLOADED = "update_download_file";
    protected static final String TYPE_NAME = "Shared_Marketing.apk";

    protected class DeleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            interrupted = true;
            handler.sendEmptyMessage(TYPE_FINISHED);
            stopSelf();
        }
    }

    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            removeCallbacksAndMessages(null);
            switch (msg.what) {
                case TYPE_DOWNLOADING:
                    if (interrupted) {
                        manager.cancel(TYPE_ID);
                    } else {
                        manager.notify(
                                TYPE_ID,
                                builder.setAutoCancel(false)
                                        .setContentText(formatToMegaBytes(length) + "M/" + formatToMegaBytes(fileLength) + "M")
                                        .setProgress(100, (int) (length * 100 / fileLength), false)
                                        .build());
                    }
                    break;
                case TYPE_FINISHED:
                    manager.cancel(TYPE_ID);
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        versionCode = intent.getStringExtra("versionCode");
        if (url == null || TextUtils.isEmpty(url)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        deleteReceiver = new DeleteReceiver();
        getApplicationContext().registerReceiver(deleteReceiver, new IntentFilter(TYPE_ACTION));
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(UpdateService.this)
                .setProgress(0, 0, false)
                .setAutoCancel(false)
                .setTicker(getResources().getString(R.string.app_name) + "v" + versionCode + " 正在下载")
                .setSmallIcon(getAppIconResId(getApplicationContext()))
                .setContentTitle(getResources().getString(R.string.app_name) + "v" + versionCode + " 正在下载...")
                .setContentText("")
                .setDeleteIntent(PendingIntent.getBroadcast(getApplicationContext(), 3, new Intent(TYPE_ACTION), PendingIntent.FLAG_CANCEL_CURRENT));
        download(url);
        return START_NOT_STICKY;
    }

    /**
     * 开始下载。
     */
    private void download(String url) {
        // 开始下载了，但是任务没有完成，代表正在下载，那么暂停下载。
        if (mDownloadRequest != null && mDownloadRequest.isStarted() && !mDownloadRequest.isFinished()) {
            // 暂停下载。
            mDownloadRequest.cancel();
        } else if (mDownloadRequest == null || mDownloadRequest.isFinished()) {// 没有开始或者下载完成了，就重新下载。
            // url 下载地址。
            // fileFolder 保存的文件夹。
            // fileName 文件名。
            // isRange 是否断点续传下载。
            // isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功。
            mDownloadRequest = NoHttp.createDownloadRequest(
                    url,
                    Environment.getExternalStorageDirectory().getAbsolutePath(),
                    TYPE_NAME,
                    true,
                    true);

            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getDownloadInstance().add(0, mDownloadRequest, downloadListener);
        }
    }

    /**
     * 下载监听
     */
    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
            length = PreferencesUtils.getLong(UpdateService.this, TYPE_DOWNLOADED, 0L);
            if (allCount != 0) {
                fileLength = allCount;
            }
            handler.sendEmptyMessage(TYPE_DOWNLOADING);
        }

        @Override
        public void onDownloadError(int what, Exception exception) {
            String message = getString(R.string.download_error);
            String messageContent;
            if (exception instanceof ServerError) {
                messageContent = getString(R.string.download_error_server);
            } else if (exception instanceof NetworkError) {
                messageContent = getString(R.string.download_error_network);
            } else if (exception instanceof StorageReadWriteError) {
                messageContent = getString(R.string.download_error_storage);
            } else if (exception instanceof StorageSpaceNotEnoughError) {
                messageContent = getString(R.string.download_error_space);
            } else if (exception instanceof TimeoutError) {
                messageContent = getString(R.string.download_error_timeout);
            } else if (exception instanceof UnKnownHostError) {
                messageContent = getString(R.string.download_error_un_know_host);
            } else if (exception instanceof URLError) {
                messageContent = getString(R.string.download_error_url);
            } else {
                messageContent = getString(R.string.download_error_un);
            }
            message = String.format(Locale.getDefault(), message, messageContent);
            CommonUtil.showToask(UpdateService.this, message);
        }

        @Override
        public void onProgress(int what, int progress, long fileCount) {
            Log.i("onProgress", "onProgress : " + fileCount);
            PreferencesUtils.putLong(UpdateService.this, TYPE_DOWNLOADED, fileCount);
            length = fileCount;
            if (length == fileLength)
                handler.sendEmptyMessage(TYPE_FINISHED);
            else
                handler.sendEmptyMessage(TYPE_DOWNLOADING);
        }

        @Override
        public void onFinish(int what, String filePath) {
            Log.i("onFinish", "onFinish : " + filePath);
            // onFinish里执行notify时不显示（方法间隔时间太短）
            installApk(new File(filePath));
        }

        @Override
        public void onCancel(int what) {
            stopSelf();
        }

        // 安装apk
        void installApk(File file) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setDataAndType(Tools.getImageContentUri(UpdateService.this, file), "application/vnd.android.package-archive");
            } else {
                // 执行的数据类型
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        }

    };

    /*public String getApplicationName(Context app) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        try {
            packageManager = app.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(app.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }*/

    public int getAppIconResId(Context app) {
        int id = 0;
        PackageManager pm = app.getPackageManager();
        String pkg = app.getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            id = ai.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String formatToMegaBytes(long bytes) {
        double megaBytes = bytes / 1048576.0;
        if (megaBytes < 1) {
            return new DecimalFormat("0.0").format(megaBytes);
        }
        return new DecimalFormat("#.0").format(megaBytes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deleteReceiver != null) getApplicationContext().unregisterReceiver(deleteReceiver);
        if (mDownloadRequest != null) mDownloadRequest.cancel();
    }
}
