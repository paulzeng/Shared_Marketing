package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.github.yoojia.qrcode.qrcode.QRCodeEncoder;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.share.Const;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.RandomLength;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MouldActivity extends BaseActivity {

    @BindView(R.id.iv_mould_img)
    ImageView iv_img;
    @BindView(R.id.tv_mould_name)
    TextView tv_name;
    @BindView(R.id.tv_mould_phone)
    TextView tv_phone;
    @BindView(R.id.iv_mould_qr)
    ImageView iv_qr;
    @BindView(R.id.fl_mould_screen)
    FrameLayout fl_screen;

    private List<String> imgs;

    // 分享
    private BottomBaseDialog dialog;
    private String mUrl;
    private String mTitle;
    private String mContent = "";

    private File mSaveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mould);
        ButterKnife.bind(this);
        setToolbarVisibility(false);
        init_title();
    }

    @Override
    public void init_title() {
        tv_name.setText(getIntent().getStringExtra("real_name"));
        tv_phone.setText(getIntent().getStringExtra("mobile"));
        imgs = getIntent().getStringArrayListExtra("list");
        mUrl = getIntent().getStringExtra("link");
        mTitle = getString(R.string.app_name);
        mContent = CommonUtil.replaceAction(getString("mobile"), "(?<=\\d{4})\\d(?=\\d{2})") + "给您推荐TA心目中最棒的买房平台【浩客通】";

        iv_qr.setImageBitmap(generateQRCode(500, mUrl));

        setMouldImg();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * attention to this below ,must add this*
         * 分享图片方式
         * imageurl       //网络图片
         * file           //本地文件
         * R.drawable.xxx //资源文件
         * bitmap         //bitmap文件
         * byte[]         //字节流
         */
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);

        if (dialog != null && dialog.isShowing()) dialog.dismiss();

        switch (v.getId()) {
            case R.id.iv_mould_right:
                if (mUrl == null) return;
                dialog = new BottomBaseDialog(baseContext) {
                    @Override
                    public View onCreateView() {
                        return View.inflate(baseContext, R.layout.dialog_mould_share, null);
                    }

                    @Override
                    public void setUiBeforShow() {
                        // TODO: 2017/2/22 0022 分享
                    }
                };
                dialog.show();
                break;
            case R.id.ll_dialog_share_wechat:
                if (mSaveFile == null) saveFile(getViewBitmap(fl_screen));

                new ShareAction(baseContext)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(mTitle)
                        .withText(mContent)
                        .withTargetUrl(mUrl)
                        .withMedia(new UMImage(baseContext, mSaveFile))
                        .share();
                break;
            case R.id.ll_dialog_share_qq:
                if (mSaveFile == null) saveFile(getViewBitmap(fl_screen));

                new ShareAction(baseContext)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(mTitle)
                        .withText(mContent)
                        .withTargetUrl(mUrl)
                        .withMedia(new UMImage(baseContext, mSaveFile))
                        .share();
                break;
            case R.id.ll_dialog_share_circle:
                if (mSaveFile == null) saveFile(getViewBitmap(fl_screen));

                new ShareAction(baseContext)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withTitle(mTitle)
                        .withText(mContent)
                        .withTargetUrl(mUrl)
                        .withMedia(new UMImage(baseContext, mSaveFile))
                        .share();
                break;
            case R.id.tv_mould_right:
                if (mSaveFile == null) saveFile(getViewBitmap(fl_screen));

                Intent intent = new Intent(this, SaveActivity.class);
                intent.putExtra("url", mUrl);
                intent.putExtra("title", mTitle);
                intent.putExtra("content", mContent);
                intent.putExtra("logo", mSaveFile.getAbsolutePath());
                startActivity(intent);
                break;
            case R.id.btn_mould_change:
                setMouldImg();
                break;
        }
    }

    private void setMouldImg() {
        int pos = RandomLength.createRandomNumber(0, imgs.size());
        Glide.with(this)
                .load(imgs.get(pos))
                .crossFade()
                .into(iv_img);
    }

    /**
     * 生成二维码
     */
    private Bitmap generateQRCode(final int size, String content) {
        return new QRCodeEncoder.Builder()
                .width(size)
                .height(size)
                .paddingPx(0)
                .marginPt(2)
                .build()
                .encode(content);
    }

    /**
     * 根据view来生成bitmap图片，可用于截图功能
     */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        // 能画缓存就返回false
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) v.destroyDrawingCache();
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    /**
     * 保存Bitmap图片为本地文件
     */
    public void saveFile(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Const.SAVE_FILE);
        if(!dir.exists()) dir.mkdirs();
        mSaveFile = new File(dir, "/模板_" + RandomLength.getRandomString(6) + ".jpg");
        try {
            if(!mSaveFile.exists()) mSaveFile.createNewFile();
            FileOutputStream out = new FileOutputStream(mSaveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            // 保存图片到相册显示的方法（没有则只有重启后才有）
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(mSaveFile);
            intent.setData(uri);
            sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
