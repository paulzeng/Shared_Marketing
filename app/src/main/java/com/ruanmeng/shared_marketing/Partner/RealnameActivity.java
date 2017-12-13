package com.ruanmeng.shared_marketing.Partner;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.CropperActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.BitmapHelper;
import com.ruanmeng.utils.PreferencesUtils;
import com.ruanmeng.utils.RandomLength;
import com.ruanmeng.utils.Tools;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class RealnameActivity extends BaseActivity {

    @BindView(R.id.et_realname_name)
    EditText et_name;
    @BindView(R.id.et_realname_id)
    EditText et_card;
    @BindView(R.id.iv_realname_front)
    ImageView iv_front;
    @BindView(R.id.iv_realname_back)
    ImageView iv_back;

    private String photo_front;
    private String photo_back;
    private int photo_type;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_PICTURE = 2;

    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realname);
        ButterKnife.bind(this);
        init_title("实名认证", "保存");
    }

    @Override
    public void init_title() {
        super.init_title();
        tvRight.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_nav_right:
                final String name = et_name.getText().toString().trim();
                final String card = et_card.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    showToask("请输入您的姓名");
                    return;
                }
                if (TextUtils.isEmpty(card)) {
                    showToask("请输入您的身份证号");
                    return;
                }
                if (photo_front == null) {
                    showToask("请上传身份证正面图片");
                    return;
                }
                if (photo_back == null) {
                    showToask("请上传身份证反面图片");
                    return;
                }
                if (card.length() < 18) {
                    showToask("请输入18位身份证号码");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.auth, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("realname", name);
                mRequest.add("id_card_no", card);
                mRequest.add("id_card_img1", photo_front);
                mRequest.add("id_card_img2", photo_back);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        PreferencesUtils.putString(baseContext, "is_auth", "1");

                        onBackPressed();
                    }
                });
                break;
            case R.id.iv_realname_front:
                photo_type = 1;
                RealnameActivityPermissionsDispatcher.needsPermissionWithCheck(RealnameActivity.this);
                break;
            case R.id.iv_realname_back:
                photo_type = 2;
                RealnameActivityPermissionsDispatcher.needsPermissionWithCheck(RealnameActivity.this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_PICTURE:
                    if (data != null) setImageToView(data);
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            throw new RuntimeException("The uri is not exist.");
        }

        Intent intent = new Intent(this, CropperActivity.class);
        intent.putExtra("path", Tools.getRealFilePath(this, uri));
        intent.putExtra("aspectX", 8);
        intent.putExtra("aspectY", 5);
        startActivityForResult(intent, CROP_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        String path = data.getStringExtra("path");
        if (path != null) {
            switch (photo_type) {
                case 1:
                    photo_front = Tools.bitmapToBase64(BitmapHelper.getImage(path, 720f, 1280f, 200));
                    Glide.with(baseContext).load(path).into(iv_front);
                    break;
                case 2:
                    photo_back = Tools.bitmapToBase64(BitmapHelper.getImage(path, 720f, 1280f, 200));
                    Glide.with(baseContext).load(path).into(iv_back);
                    break;
            }
        }
    }

    private void showDialog(String[] stringItems) {
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.isTitleShow(false)
                .lvBgColor(Color.parseColor("#FFFFFF"))
                .dividerColor(Color.parseColor("#D8D8D8"))
                .dividerHeight(0.5f)
                .itemTextColor(Color.parseColor("#007AFF"))
                .itemTextSize(15f)
                .cancelText(Color.parseColor("#007AFF"))
                .cancelTextSize(15f)
                .layoutAnimation(null)
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                switch (position) {
                    case 0: //拍照
                        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Const.SAVE_FILE);
                        if(!dir.exists()) dir.mkdirs();
                        mFile = new File(dir, "/image_" + RandomLength.getRandomString(6) + ".jpg");
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            Uri imageUri = FileProvider.getUriForFile(baseContext, "com.ruanmeng.shared_marketing.fileprovider", mFile);
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            // openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Tools.getImageContentUri(baseContext, file));
                            startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        } else {
                            // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
                            startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        }
                        break;
                    case 1: //选择本地照片
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, CHOOSE_PICTURE); // 适用于4.4及以上android版本
                        break;
                }
            }
        });
    }

    @NeedsPermission({ Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void needsPermission() {
        showDialog(new String[]{"拍照", "从相册选择"});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RealnameActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({ Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void showRationale(final PermissionRequest request) {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.content("需要使用相机和存储权限以拍摄图片和加载本地文件")
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

    @OnPermissionDenied({ Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void permissionDenied() {
        showToask("请求权限被拒绝，请重新开启权限");
    }

    @OnNeverAskAgain({ Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void neverAskAgain() {
        showToask("不再询问权限");
    }
}
