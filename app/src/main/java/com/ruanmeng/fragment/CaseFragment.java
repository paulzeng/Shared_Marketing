package com.ruanmeng.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.dialog.nohttp.CallServer;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.CropperActivity;
import com.ruanmeng.shared_marketing.Partner.PasswordActivity;
import com.ruanmeng.shared_marketing.Partner.SettingActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.BitmapHelper;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.ruanmeng.utils.RandomLength;
import com.ruanmeng.utils.Tools;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
@RuntimePermissions
public class CaseFragment extends Fragment {

    @BindView(R.id.iv_fragment_case_img)
    RoundedImageView iv_img;
    @BindView(R.id.tv_fragment_case_name)
    TextView tv_name;
    @BindView(R.id.tv_fragment_case_type)
    TextView tv_type;
    @BindView(R.id.stv_fragment_case_phone)
    SuperTextView tv_phone;
    @BindView(R.id.stv_fragment_case_gender)
    SuperTextView tv_gender;
    @BindView(R.id.stv_fragment_case_password)
    SuperTextView tv_pwd;

    private Request<String> mRequest;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_PICTURE = 2;

    private File mFile;

    //调用这个方法切换时不会释放掉Fragment
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_case, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        String logo = PreferencesUtils.getString(getActivity(), "logo");
        if (!TextUtils.isEmpty(logo)) {
            Glide.with(getActivity())
                    .load(logo)
                    .placeholder(R.mipmap.personal_a1) // 等待时的图片
                    .error(R.mipmap.personal_a1) // 加载失败的图片
                    .crossFade()
                    .dontAnimate()
                    .into(iv_img);
            iv_img.setTag(R.id.image_tag, logo);
        }

        String userName = PreferencesUtils.getString(getActivity(), "user_name");
        String realName = PreferencesUtils.getString(getActivity(), "real_name");
        tv_name.setText(TextUtils.isEmpty(realName) ? userName : realName);
        tv_phone.setRightString(PreferencesUtils.getString(getActivity(), "mobile"));
        tv_gender.setRightString(TextUtils.equals(PreferencesUtils.getString(getActivity(), "gender"), "1") ? "男" : "女");

        switch (PreferencesUtils.getString(getActivity(), "user_type")) {
            case "1":
                tv_type.setText("案场经理");
                break;
            case "2":
                tv_type.setText("置业顾问");
                break;
            case "3":
                tv_type.setText("驻场经理");
                break;
            case "7":
                tv_type.setText("合作单位合伙人");
                break;
            case "8":
                tv_type.setText("渠道经理");
                break;
            case "9":
                tv_type.setText("分销专员");
                break;
            case "10":
                tv_type.setText("渠道专员");
                break;
            case "11":
                tv_type.setText("司机");
                break;
        }
    }

    @OnClick({ R.id.tv_fragment_case_setting,
               R.id.iv_fragment_case_img,
               R.id.stv_fragment_case_gender,
               R.id.stv_fragment_case_password })
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_fragment_case_setting:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_fragment_case_img:
                CaseFragmentPermissionsDispatcher.needsPermissionWithCheck(CaseFragment.this);
                break;
            case R.id.stv_fragment_case_gender:
                final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), new String[]{ "男", "女" }, null);
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
                        mRequest.add("user_id", PreferencesUtils.getString(getActivity(), "user_id"));
                        mRequest.add("gender", position + 1);
                        mRequest.add("token", MD5Util.md5(Const.timeStamp));
                        mRequest.add("time", String.valueOf(Const.timeStamp));

                        // 添加到请求队列
                        CallServer.getRequestInstance().add(getActivity(), mRequest,
                                new CustomHttpListener<JSONObject>(getActivity(), true, null) {
                            @Override
                            public void doWork(JSONObject data, String code) {
                                tv_gender.setRightString(position == 0 ? "男": "女");
                                PreferencesUtils.putString(getActivity(), "gender", String.valueOf(position + 1));
                            }
                        }, true);
                    }
                });
                break;
            case R.id.stv_fragment_case_password:
                intent = new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

        Intent intent = new Intent(getActivity(), CropperActivity.class);
        intent.putExtra("path", Tools.getRealFilePath(getActivity(), uri));
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        startActivityForResult(intent, CROP_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        String path = data.getStringExtra("path");
        if (path != null) {
            mRequest = NoHttp.createStringRequest(HttpIP.modifyLogo, Const.POST);
            mRequest.add("user_id", PreferencesUtils.getString(getActivity(), "user_id"));
            mRequest.add("image", Tools.bitmapToBase64(BitmapHelper.getImage(path, 200)));
            mRequest.add("token", MD5Util.md5(Const.timeStamp));
            mRequest.add("time", String.valueOf(Const.timeStamp));

            // 添加到请求队列
            CallServer.getRequestInstance().add(getActivity(), mRequest,
                    new CustomHttpListener<JSONObject>(getActivity(), true, null) {
                @Override
                public void doWork(JSONObject data, String code) {
                    try {
                        String logo = data.getJSONObject("data").getString("logo");
                        PreferencesUtils.putString(getActivity(), "logo", logo);

                        Glide.with(getActivity()).load(logo).into(iv_img);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, true);

        }
    }

    private void showDialog(String[] stringItems) {
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, null);
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
                        mFile = new File(
                                Environment.getExternalStorageDirectory() + "/" + Const.SAVE_FILE,
                                "image_" + RandomLength.getRandomString(6) + ".jpg");
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            Uri imageUri = FileProvider.getUriForFile(getActivity(), "com.ruanmeng.shared_marketing.fileprovider", mFile);
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
        CaseFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({ Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void showRationable(final PermissionRequest request) {
        final MaterialDialog materialDialog = new MaterialDialog(getActivity());
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
        CommonUtil.showToask(getActivity(), "请求权限被拒绝，请重新开启权限");
    }

    @OnNeverAskAgain({ Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE })
    void neverAskAgain() {
        CommonUtil.showToask(getActivity(), "不再询问权限");
    }
}
