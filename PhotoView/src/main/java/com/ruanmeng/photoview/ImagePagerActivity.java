package com.ruanmeng.photoview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePagerActivity extends FragmentActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String EXTRA_IMAGE_SAVE = "image_save";
	private String SAVE_FILE = "Public_Music";

	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator;
	private String[] urls;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_pager);

		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
		boolean isSave = getIntent().getBooleanExtra(EXTRA_IMAGE_SAVE, true);

		if (pagerPosition >= urls.length) pagerPosition = urls.length - 1;

		TextView tv_save = (TextView) findViewById(R.id.tv_imagepager_save);
		indicator = (TextView) findViewById(R.id.tv_imagepager_indicator);
		mPager = (HackyViewPager) findViewById(R.id.hv_imagepager_img);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);

		if(!isSave) tv_save.setVisibility(View.INVISIBLE);

		// 更新下标
		mPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) { }

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				CharSequence text = getString(
						R.string.viewpager_indicator,
						position + 1,
						mPager.getAdapter().getCount());
				indicator.setText(text);

				pagerPosition = position;
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

	public void doClick(View v) {
		Glide.with(this)
				.load(urls[pagerPosition])
				.asBitmap()
				.into(new SimpleTarget<Bitmap>() {
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
						File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), SAVE_FILE);
						if(!dir.exists()) dir.mkdirs();
						File file = new File(dir, urls[pagerPosition].substring(urls[pagerPosition].lastIndexOf("/")));
						try {
							if(!file.exists())
								file.createNewFile();
							else {
								Toast.makeText(ImagePagerActivity.this, "已保存", Toast.LENGTH_SHORT).show();
								return;
							}
							FileOutputStream out = new FileOutputStream(file);
							resource.compress(Bitmap.CompressFormat.PNG, 100, out);
							out.flush();
							out.close();
							Toast.makeText(
									ImagePagerActivity.this,
									"图片已保存到" + file.getAbsolutePath(),
									Toast.LENGTH_SHORT).show();

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
			String url = fileList[position];
			return ImageDetailFragment.newInstance(url);
		}

	}
}