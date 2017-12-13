package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.OnItemSelectListener;
import com.jude.rollviewpager.RollPagerView;
import com.ruanmeng.adapter.LoopAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.view.WrapWebView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HouseTypeActivity extends BaseActivity {

    @BindView(R.id.rp_house_type_banner)
    RollPagerView mBannerRoll;
    @BindView(R.id.tv_house_type_banner)
    TextView tv_banner;
    @BindView(R.id.tv_house_type_name)
    TextView tv_name;
    @BindView(R.id.tv_house_type_status)
    TextView tv_status;
    @BindView(R.id.tv_house_type_room)
    TextView tv_room;
    @BindView(R.id.tv_house_type_price)
    TextView tv_price;
    @BindView(R.id.wv_house_type_des)
    WrapWebView wv_content;

    private LoopAdapter mLoopAdapter;
    private List<String> imgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_type);
        ButterKnife.bind(this);
        init_title("户型详情");

        BankItem info = (BankItem) getIntent().getSerializableExtra("content");
        if (info != null) {
            imgs.addAll(info.getImages_more());
            tv_banner.setText("1/" + imgs.size());
            mLoopAdapter.setImgs(imgs);
            if (imgs.size() <= 1) mBannerRoll.pause();

            String str = "<meta " +
                    "name=\"viewport\" " +
                    "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                    "<style>" +
                    ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.1em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                    "img{ max-width: 100% !important;display:block;height:auto !important;}" +
                    "*{ max-width:100% !important;}\n" +
                    "</style>";
            String des = info.getDetails();
            wv_content.loadDataWithBaseURL(HttpIP.IP, str + "<div class=\"con\">" + des + "</div>", "text/html", "utf-8", "");

            tv_name.setText(info.getType_name() + "（" + info.getHouse_type() + "）");
            tv_price.setText(info.getPrice() + "起");
            tv_room.setText(info.getHall_type() + " " + info.getArea() + "㎡");
            tv_status.setText(TextUtils.equals("1", info.getSts()) ? "在售" : "停售");
        }
    }

    @Override
    public void init_title() {
        super.init_title();
        wv_content.getSettings().setJavaScriptEnabled(true);
        wv_content.getSettings().setJavaScriptEnabled(true); //设置WebView属性，能够执行Javascript脚本
        wv_content.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //自动打开窗口
        wv_content.getSettings().setLoadWithOverviewMode(true); //设置WebView可以加载更多格式页面
        // 设置出现缩放工具
        wv_content.getSettings().setBuiltInZoomControls(true);
        wv_content.getSettings().setDisplayZoomControls(false);

        mLoopAdapter = new LoopAdapter(this, mBannerRoll);
        mBannerRoll.setAdapter(mLoopAdapter);
        mBannerRoll.setHintView(null);
        mBannerRoll.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(baseContext, PhotoActivity.class);
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_URLS, imgs.toArray(new String[imgs.size()]));
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_INDEX, position);
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_SAVE, false);
                intent.putExtra("isShow", false);
                startActivity(intent);
            }
        });
        mBannerRoll.setOnItemSelectListener(new OnItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                tv_banner.setText((position + 1) + "/" + imgs.size());
            }
        });
    }
}
