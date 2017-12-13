package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.shared_marketing.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

import butterknife.ButterKnife;

public class SaveActivity extends BaseActivity {

    // 分享
    private String mUrl;
    private String mTitle;
    private String mContent;
    private String mLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        ButterKnife.bind(this);
        init_title("保存/分享");

        mUrl = getIntent().getStringExtra("url");
        mTitle = getIntent().getStringExtra("title");
        mContent = getIntent().getStringExtra("content");
        mLogo = getIntent().getStringExtra("logo");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.ll_save_wechat:
                new ShareAction(baseContext)
                        .setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(mTitle)
                        .withText(mContent)
                        .withTargetUrl(mUrl)
                        .withMedia(new UMImage(baseContext, new File(mLogo)))
                        .share();
                break;
            case R.id.ll_save_qq:
                new ShareAction(baseContext)
                        .setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(mTitle)
                        .withText(mContent)
                        .withTargetUrl(mUrl)
                        .withMedia(new UMImage(baseContext, new File(mLogo)))
                        .share();
                break;
            case R.id.ll_save_circle:
                new ShareAction(baseContext)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .withTitle(mTitle)
                        .withText(mContent)
                        .withTargetUrl(mUrl)
                        .withMedia(new UMImage(baseContext, new File(mLogo)))
                        .share();
                break;
            case R.id.btn_save_more:
                onBackPressed();
                break;
        }
    }
}
