/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dialog.nohttp;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.ruanmeng.nohttp.HttpListener;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.R;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ParseError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.RestResponse;

import java.net.ProtocolException;

public class HttpResponseDialogListener<T> implements OnResponseListener<T> {

    private Context context;

    /**
     * Dialog
     */
    private KProgressHUD hud;

    /**
     * 当前请求
     */
    private Request<?> mRequest;

    /**
     * 结果回调
     */
    private HttpListener<T> callback;

    /**
     * 是否显示dialog
     */
    private boolean isLoading;

    /**
     * @param context      context用来实例化dialog
     * @param request      请求对象
     * @param httpCallback 回调对象
     * @param canCancel    是否允许用户取消请求
     * @param isLoading    是否显示dialog
     */
    public HttpResponseDialogListener(Context context, Request<?> request, HttpListener<T> httpCallback, boolean canCancel, boolean isLoading) {
        this.mRequest = request;
        if (context != null && isLoading) {
            hud = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("正在请求，请稍候…")
                    .setCancellable(canCancel)
                    .setDimAmount(0.5f);
            hud.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mRequest.cancel();
                }
            });
        }
        this.callback = httpCallback;
        this.isLoading = isLoading;
        this.context = context;
    }

    /**
     * 开始请求, 这里显示一个dialog
     */
    @Override
    public void onStart(int what) {
        if (isLoading && hud != null && !hud.isShowing())
            hud.show();
    }

    /**
     * 结束请求, 这里关闭dialog
     */
    @Override
    public void onFinish(int what) {
        if (isLoading && hud != null && hud.isShowing())
            hud.dismiss();
    }

    /**
     * 成功回调
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null) {
            // 这里判断一下http响应码，这个响应码问下你们的服务端你们的状态有几种，一般是200成功。
            // w3c标准http响应码：http://www.w3school.com.cn/tags/html_ref_httpmessages.asp

            int code = response.responseCode();
            if (code == 200 || code == 304) { // 如果使用http标准的304重定向到缓存的话，还要判断下304状态码。
                callback.onSucceed(what, response);
            } else { // 如果
                Response<T> error = new RestResponse<>(response.request(),
                        response.isFromCache(),
                        response.getHeaders(),
                        null,
                        response.getNetworkMillis(),
                        new ParseError("数据错误")); // 这里可以传一个你的自定义异常。
                onFailed(what, error); // 去让错误的回调处理。
            }
        }
    }

    /**
     * 失败回调
     */
    @Override
    public void onFailed(int what, Response<T> response) {
        Exception exception = response.getException();
        if (exception instanceof NetworkError) {// 网络不好
            Toast.makeText(context, R.string.error_please_check_network, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof TimeoutError) {// 请求超时
            Toast.makeText(context, R.string.error_timeout, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            Toast.makeText(context, R.string.error_not_found_server, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof URLError) {// URL是错的
            Toast.makeText(context, R.string.error_url_error, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            Toast.makeText(context, R.string.error_not_found_cache, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof ProtocolException) {
            Toast.makeText(context, R.string.error_system_unsupport_method, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof ParseError) {
            Toast.makeText(context, R.string.error_parse_data_error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.error_unknow, Toast.LENGTH_SHORT).show();
        }
        Logger.e("错误：" + exception.getMessage());
        if (callback != null)
            callback.onFailed(what, response);
    }

}
