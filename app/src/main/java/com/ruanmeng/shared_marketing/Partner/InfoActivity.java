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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.makeramen.roundedimageview.RoundedImageView;
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

import org.json.JSONException;
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
public class InfoActivity extends BaseActivity {

    @BindView(R.id.iv_info_img)
    RoundedImageView iv_img;
    @BindView(R.id.tv_info_nickname)
    TextView tv_name;
    @BindView(R.id.tv_info_gender)
    TextView tv_gender;
    @BindView(R.id.tv_info_phone)
    TextView tv_phone;
    @BindView(R.id.iv_info_real)
    ImageView iv_auth;
    @BindView(R.id.tv_info_real)
    TextView tv_auth;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_PICTURE = 2;
    private String isAuth;

    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        init_title("个人资料");

        tv_phone.setText(PreferencesUtils.getString(this, "mobile"));

        Glide.with(this)
                .load(PreferencesUtils.getString(this, "logo"))
                .placeholder(R.mipmap.personal_a20) // 等待时的图片
                .error(R.mipmap.personal_a20) // 加载失败的图片
                .crossFade()
                .dontAnimate()
                .into(iv_img);
    }

    @Override
    protected void onStart() {
        super.onStart();

        tv_name.setText(PreferencesUtils.getString(this, "user_name"));
        tv_gender.setText(TextUtils.equals(PreferencesUtils.getString(this, "gender"), "1") ? "男" : "女");
        isAuth = PreferencesUtils.getString(this, "is_auth");

        switch (isAuth) {
            case "0":
                tv_auth.setText("未认证");
                break;
            case "1":
                tv_auth.setText("审核中");
                break;
            case "2":
                tv_auth.setText("已认证");
                iv_auth.setVisibility(View.VISIBLE);
                break;
            case "3":
                tv_auth.setText("未通过");
                break;
        }

    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.ll_info_photo:
                InfoActivityPermissionsDispatcher.needsPermissionWithCheck(InfoActivity.this);
                break;
            case R.id.ll_info_nickname:
                startActivity(NicknameActivity.class);
                break;
            case R.id.ll_info_gender:
                final ActionSheetDialog dialog = new ActionSheetDialog(this, new String[]{ "男", "女" }, null);
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
                    public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        dialog.dismiss();

                        mRequest = NoHttp.createStringRequest(HttpIP.modifyGender, Const.POST);
                        mRequest.add("user_id", PreferencesUtils.getString(baseContext, "user_id"));
                        mRequest.add("gender", position + 1);

                        getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                            @Override
                            public void doWork(JSONObject data, String code) {
                                tv_gender.setText(position == 0 ? "男": "女");
                                PreferencesUtils.putString(baseContext, "gender", String.valueOf(position + 1));
                            }
                        });
                    }
                });
                break;
            case R.id.ll_info_real:
                switch (isAuth) {
                    case "0":
                        startActivity(RealnameActivity.class);
                        break;
                    case "1":
                        showToask("实名信息正在审核中");
                        break;
                    case "2":
                        startActivity(RealRightActivity.class);
                        break;
                    case "3":
                        startActivity(RealWrongActivity.class);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        startActivityForResult(intent, CROP_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        if (data == null) return;
        String path = data.getStringExtra("path");
        if (path != null) {
            mRequest = NoHttp.createStringRequest(HttpIP.modifyLogo, Const.POST);
            mRequest.add("user_id", PreferencesUtils.getString(baseContext, "user_id"));
            mRequest.add("image", Tools.bitmapToBase64(BitmapHelper.getImage(path, 200)));

            getRequest(new CustomHttpListener<JSONObject>(baseContext, true, null) {
                @Override
                public void doWork(JSONObject data, String code) {
                    try {
                        String logo = data.getJSONObject("data").getString("logo");
                        PreferencesUtils.putString(baseContext, "logo", logo);

                        Glide.with(baseContext).load(logo).into(iv_img);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

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
        InfoActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
