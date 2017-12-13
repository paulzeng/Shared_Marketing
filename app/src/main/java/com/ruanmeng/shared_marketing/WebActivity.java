package com.ruanmeng.shared_marketing;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Partner.LowerActivity;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebActivity extends BaseActivity {

    @BindView(R.id.wv_web_web)
    WebView wv_Web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        init_title("用户服务协议、积分规则、佣金说明、用户协议、发展规则、提现协议、推荐用户说明、轮播详情、会员抽奖专区");
        // type 1、4、5、1、6、7、8、9、10
        tvTitle.setText(getIntent().getStringExtra("title"));

        switch (getIntent().getStringExtra("type")) {
            case "2":
                String str = "<!doctype html><html>\n" +
                        "<meta charset=\"utf-8\">" +
                        "<style type=\"text/css\">" +
                        "body{padding:0; margin:0;}\n" +
                        ".view_h1{ width:90%; margin:0 auto; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                        ".view_time{ width:90%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999;}\n" +
                        ".con{width:90%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                        "img{width:auto; max-width: 100% !important;height:auto !important;margin:0 auto;display:block;}\n" +
                        "</style>\n" +
                        "<body style=\"padding:0; margin:0; \">" +
                        "<div class=\"view_h1\">"+
                        getIntent().getStringExtra("title") +
                        "</div>" +
                        "<div class=\"view_time\" style=\"border-bottom:1px solid #e0e0e0; padding-bottom:5px;\">"+
                        getIntent().getStringExtra("create_time") +
                        "</div>" +
                        "<div class=\"con\">"+
                        getIntent().getStringExtra("content") +
                        "</div>" +
                        "</body>" +
                        "</html>";

                wv_Web.loadDataWithBaseURL(HttpIP.IP, str, "text/html", "utf-8", "");
                break;
            case "8":
                getInfoData();
                break;
            case "9":
                wv_Web.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                wv_Web.loadUrl(getIntent().getStringExtra("url"));
                break;
            case "10":
                wv_Web.addJavascriptInterface(new JsInteration(), "android");
                wv_Web.setWebChromeClient(new WebChromeClient());
                wv_Web.setWebViewClient(new WebViewClient() {

                    /* 这个事件，将在用户点击链接时触发。
                     * 通过判断url，可确定如何操作，
                     * 如果返回true，表示我们已经处理了这个request，
                     * 如果返回false，表示没有处理，那么浏览器将会根据url获取网页
                     */
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true; // 表示已经处理了这次URL的请求
                    }

                });

                wv_Web.loadUrl(getIntent().getStringExtra("url"));
                break;
            default:
                getData();
                break;
        }
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

    private void getInfoData() {
        mRequest = NoHttp.createStringRequest(HttpIP.recCusexplain, Const.POST);
        getRequest(new CustomHttpListener<JSONObject>(this, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    JSONObject obj = data.getJSONObject("data");

                    String str = "<!doctype html><html>\n" +
                            "<meta charset=\"utf-8\">" +
                            "<style type=\"text/css\">" +
                            "body{ padding:0; margin:0;}\n" +
                            ".con{ width:90%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                            ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                            "img{ width:auto; max-width: 100% !important;height:auto !important;margin:0 auto;display:block;}\n" +
                            "*{ max-width:100% !important;}\n" +
                            "</style>\n" +
                            "<body style=\"padding:0; margin:0; \">" +
                            "<div class=\"con\">" +
                            obj.getString("content") +
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
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.contentShow, Const.POST);
        mRequest.add("type", getIntent().getStringExtra("type"));
        if (PreferencesUtils.getBoolean(getApplicationContext(), "isLogin")) {
            mRequest.add("role_type", getString("user_type"));
        } else {
            mRequest.add("role_type", "6");
        }

        getRequest(new CustomHttpListener<JSONObject>(this, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    JSONArray arr = data.getJSONArray("data");

                    switch (getIntent().getStringExtra("type")) {
                        case "1":
                        case "4":
                        case "5":
                        case "6":
                        case "7":
                            String str = "<!doctype html><html>\n" +
                                    "<meta charset=\"utf-8\">" +
                                    "<style type=\"text/css\">" +
                                    "body{ padding:0; margin:0;}\n" +
                                    ".con{ width:90%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                    "img{ width:auto; max-width: 100% !important;height:auto !important;margin:0 auto;display:block;}\n" +
                                    "*{ max-width:100% !important;}\n" +
                                    "</style>\n" +
                                    "<body style=\"padding:0; margin:0; \">" +
                                    "<div class=\"con\">" +
                                    arr.getJSONObject(0).getString("content") +
                                    "</div>" +
                                    "</body>" +
                                    "</html>";

                            wv_Web.loadDataWithBaseURL(HttpIP.IP, str, "text/html", "utf-8", "");
                            break;
                        default:
                            String str_def = "<!doctype html><html>\n" +
                                    "<meta charset=\"utf-8\">" +
                                    "<style type=\"text/css\">" +
                                    "body{padding:0; margin:0;}\n" +
                                    ".view_h1{ width:90%; margin:0 auto; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                                    ".view_time{ width:90%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999;}\n" +
                                    ".con{width:90%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                    ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                    "img{width:auto; max-width: 100% !important;height:auto !important;margin:0 auto;display:block;}\n" +
                                    "*{ max-width:100% !important;}\n" +
                                    "</style>\n" +
                                    "<body style=\"padding:0; margin:0; \">" +
                                    "<div class=\"view_h1\">"+
                                    arr.getJSONObject(0).getString("title") +
                                    "</div>" +
                                    "<div class=\"view_time\" style=\"border-bottom:1px solid #e0e0e0; padding-bottom:5px;\">"+
                                    arr.getJSONObject(0).getString("create_time") +
                                    "</div>" +
                                    "<div class=\"con\">"+
                                    arr.getJSONObject(0).getString("content") +
                                    "</div>" +
                                    "</body>" +
                                    "</html>";

                            wv_Web.loadDataWithBaseURL(HttpIP.IP, str_def, "text/html", "utf-8", "");
                            break;
                    }
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
    public void onBackPressed() {
        if (wv_Web.canGoBack()) wv_Web.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv_Web.destroy();
    }

    private class JsInteration {
        @JavascriptInterface
        public void startFunction() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(LowerActivity.class);
                }
            });
        }
    }

}
