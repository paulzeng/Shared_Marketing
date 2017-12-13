package com.ruanmeng.shared_marketing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.edmodo.cropper.CropImageView;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.share.Const;
import com.ruanmeng.utils.BitmapHelper;
import com.ruanmeng.utils.RandomLength;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropperActivity extends BaseActivity {

    private CropImageView iv_img;

    private KProgressHUD hud;
    private String path, save_path;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (hud != null) {
                hud.dismiss();
            }
            if (!TextUtils.isEmpty(save_path)) {
                Intent intent = new Intent();
                intent.putExtra("path", save_path);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        setToolbarVisibility(false);
        init_title();

        path = getIntent().getStringExtra("path");
        Glide.with(this)
                .load(path)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        iv_img.setImageBitmap(resource);
                    }
                });
    }

    @Override
    public void init_title() {
        int aspectRatioX = getIntent().getIntExtra("aspectX", 4);
        int aspectRatioY = getIntent().getIntExtra("aspectY", 3);

        iv_img = (CropImageView) findViewById(R.id.civ_cropper_img);
        iv_img.setAspectRatio(aspectRatioX, aspectRatioY);
        iv_img.setFixedAspectRatio(true);
    }

    @Override
    public void doClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cropper_cancel:
                onBackPressed();
                break;
            case R.id.tv_cropper_sure:
                hud = KProgressHUD.create(this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("正在剪切，请稍候…")
                        .setCancellable(false)
                        .setDimAmount(0.5f)
                        .show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Bitmap croppedImage = iv_img.getCroppedImage();
                        saveCropper(croppedImage);

                        handler.sendEmptyMessage(0);
                    }
                }.start();

                break;
        }
    }

    private void saveCropper(Bitmap croppedImage) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Const.SAVE_FILE);
        if(!dir.exists()) dir.mkdirs();
        File file = new File(dir, "/crop_" + RandomLength.getRandomString(6) + path.substring(path.lastIndexOf("/") + 1));
        try {
            if(!file.exists()) file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            Bitmap compressImage = BitmapHelper.compressImage(croppedImage, 300);
            compressImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            // 保存图片到相册显示的方法（没有则只有重启后才有）
            // Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            // Uri uri = Uri.fromFile(file);
            // intent.setData(uri);
            // sendBroadcast(intent);

            save_path = file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
