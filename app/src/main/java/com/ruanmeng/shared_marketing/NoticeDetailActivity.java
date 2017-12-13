package com.ruanmeng.shared_marketing;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.utils.TimeHelper;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeDetailActivity extends BaseActivity {

    @BindView(R.id.wv_notice_detail_web)
    WebView wv_Web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        ButterKnife.bind(this);
        init_title("公告详情");

        getData();
    }

    @Override
    public void init_title() {
        super.init_title();

        // 支持javascript
        wv_Web.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        wv_Web.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        wv_Web.getSettings().setBuiltInZoomControls(true);
        wv_Web.getSettings().setDisplayZoomControls(false);
        // 自适应屏幕
        // wv_Web.getSettings().setUseWideViewPort(true);
        wv_Web.getSettings().setLoadWithOverviewMode(true);
        wv_Web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv_Web.setHorizontalScrollBarEnabled(false);
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.noticeDetail, Const.POST);
        mRequest.add("id", getIntent().getStringExtra("id"));

        getRequest(new CustomHttpListener<JSONObject>(this, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {

                    String str = "<!doctype html><html>\n" +
                            "<meta charset=\"utf-8\">" +
                            "<style type=\"text/css\">" +
                            "body" +
                            "{padding:0; margin:0;}\n" +
                            ".view_h1{ width:90%; margin:0 auto; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                            ".view_time{ width:90%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999;}\n" +
                            ".con{width:90%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                            ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                            "img{width:auto; max-width: 100% !important;height:auto !important;margin:0 auto;display:block;}\n" +
                            "*{ max-width:100% !important;}\n" +
                            "</style>\n" +
                            "<body style=\"padding:0; margin:0; \">" +
                            "<div class=\"view_h1\">"+
                            data.getJSONObject("data").getString("title") +
                            "</div>" +
                            "<div class=\"view_time\" style=\"border-bottom:1px solid #e0e0e0; padding-bottom:5px;\">"+
                            data.getJSONObject("data").getString("create_time") +
                            "</div>" +
                            "<div class=\"con\">"+
                            data.getJSONObject("data").getString("content") +
                            "</div>" +
                            "</body>" +
                            "</html>";

                    wv_Web.loadDataWithBaseURL(HttpIP.IP, str, "text/html", "utf-8", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        wv_Web.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wv_Web.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv_Web.destroy();
    }

}
