package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.photoview.HackyViewPager;
import com.ruanmeng.photoview.ImageDetailFragment;
import com.ruanmeng.share.Const;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.CommonUtil;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.RecyclerIndicatorView;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotoActivity extends BaseActivity {

    @BindView(R.id.tv_nav_photo_title)
    TextView tv_title;
    @BindView(R.id.tv_photo_save)
    TextView tv_save;
    @BindView(R.id.tv_photo_hint)
    TextView tv_hint;
    @BindView(R.id.hv_photo_img)
    HackyViewPager mPager;
    @BindView(R.id.siv_photo_indicator)
    RecyclerIndicatorView indicator;

    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String EXTRA_IMAGE_TITLES = "image_titles";
    public static final String EXTRA_IMAGE_SAVE = "image_save";

    private int pagerPosition, titlePosition;
    private String[] urls;
    private String[] titles;
    private boolean isShowBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        transparentStatusBar(false);

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        boolean isSave = getIntent().getBooleanExtra(EXTRA_IMAGE_SAVE, true);
        isShowBottom = getIntent().getBooleanExtra("isShow", true);
        if (pagerPosition >= urls.length) pagerPosition = urls.length - 1;
        if(!isSave) tv_save.setVisibility(View.INVISIBLE);

        mPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), urls));
        CharSequence text = getString(com.ruanmeng.photoview.R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        tv_title.setText(text);
        if (isShowBottom) {
            titles = getIntent().getStringArrayExtra(EXTRA_IMAGE_TITLES);
            setIndicator(indicator);
        } else {
            tv_hint.setVisibility(View.GONE);
            indicator.setVisibility(View.GONE);
        }

        // 更新下标
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) { }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(
                        com.ruanmeng.photoview.R.string.viewpager_indicator,
                        position + 1,
                        mPager.getAdapter().getCount());
                tv_title.setText(text);

                pagerPosition = position;

                if (isShowBottom) {
                    List<String> list = new ArrayList<>();
                    for (String url : urls) {
                        if (TextUtils.equals(urls[pagerPosition].substring(0, 3), url.substring(0, 3))) {
                            list.add(url);
                        }
                    }

                    CharSequence hint_str = getString(
                            com.ruanmeng.photoview.R.string.viewpager_indicator,
                            list.indexOf(urls[pagerPosition]) + 1,
                            list.size());
                    tv_hint.setText(urls[position].substring(0, 3) + hint_str);

                    for (int i = 0; i < titles.length; i++) {
                        if (TextUtils.equals(urls[pagerPosition].substring(0, 3), titles[i])) {
                            if (titlePosition != i) indicator.setCurrentItem(i, true);
                            break;
                        }
                    }
                }
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private void setIndicator(final Indicator indicator) {
        indicator.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_tab_top, parent, false);
                }
                TextView textView = (TextView) convertView;
                //用了固定宽度可以避免TextView文字大小变化，tab宽度变化导致tab抖动现象
                textView.setWidth(CommonUtil.getScreenWidth(baseContext) / 5);
                textView.setText(titles[position]);
                textView.setTextSize(14f);
                return convertView;
            }
        });

        int selectColor = getResources().getColor(R.color.colorAccent);
        int unSelectColor = getResources().getColor(R.color.light);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor));
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                titlePosition = select;

                if (TextUtils.equals(titles[select], urls[pagerPosition].substring(0, 3))) return;

                for (int i = 0 ; i < urls.length ; i++) {
                    if (TextUtils.equals(titles[select], urls[i].substring(0, 3))) {
                        mPager.setCurrentItem(i);
                        break;
                    }
                }
            }
        });

        List<String> list = new ArrayList<>();
        for (String url : urls) {
            if (TextUtils.equals(urls[pagerPosition].substring(0, 3), url.substring(0, 3))) {
                list.add(url);
            }
        }

        CharSequence hint_str = getString(
                com.ruanmeng.photoview.R.string.viewpager_indicator,
                list.indexOf(urls[pagerPosition]) + 1,
                list.size());
        tv_hint.setText(urls[pagerPosition].substring(0, 3) + hint_str);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_photo_save:
                Glide.with(this)
                        .load(urls[pagerPosition].substring(3))
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Const.SAVE_FILE);
                                if(!dir.exists()) dir.mkdirs();
                                File file = new File(dir, urls[pagerPosition].substring(urls[pagerPosition].lastIndexOf("/")));
                                try {
                                    if(!file.exists()) file.createNewFile();
                                    else {
                                        showToask("已保存");
                                        return;
                                    }
                                    FileOutputStream out = new FileOutputStream(file);
                                    resource.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.flush();
                                    out.close();
                                    showToask("图片已保存到" + file.getAbsolutePath());

                                    // 保存图片到相册显示的方法（没有则只有重启后才有）
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(file);
                                    intent.setData(uri);
                                    sendBroadcast(intent);

                                    // 第二种方式
							        /*MediaScannerConnection.scanFile(
									ImagePagerActivity.this,
									new String[]{file.getAbsolutePath()},
									null,
									null);*/
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                break;
        }
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        private String[] fileList;

        private ImagePagerAdapter(FragmentManager fm, String[] fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.length;
        }

        @Override
        public Fragment getItem(int position) {
            String url = isShowBottom ? fileList[position].substring(3) : fileList[position];
            return ImageDetailFragment.newInstance(url);
        }

    }

}
