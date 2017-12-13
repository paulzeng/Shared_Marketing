package com.ruanmeng.nohttp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class CustomHttpListener<T> implements HttpListener<String> {

	private JSONObject object;
	private Context context;
	private boolean isGson;
	private Class<T> dataM;

	public CustomHttpListener(Context context, boolean isGson, Class<T> dataM) {
		this.context = context;
		this.isGson = isGson;
		this.dataM = dataM;
	}

	@Override
	public void onSucceed(int what, Response<String> response) {
		Log.i("onSucceed", "请求成功：\n" + response.get());

		if (!response.get().matches("^\\{(.+:.+,*){1,}\\}$")) {
			Toast.makeText(context, "网络数据格式错误", Toast.LENGTH_SHORT).show();
			// onFinally(new JSONObject(), "-2", true); // JSON数据格式错误
			return;
		}

		try {
			object = new JSONObject(response.get());
			if(dataM == null && "0".equals(object.getString("msgcode"))) {
				Toast.makeText(context, object.getString("msg"), Toast.LENGTH_SHORT).show();
				return;
			}
			if(!"0".equals(object.getString("msgcode"))) {
				if(isGson && dataM != null) {
					Gson gson = new Gson();
					doWork(gson.fromJson(object.toString(), dataM), object.getString("msgcode"));
				} else {
					doWork((T) object, object.getString("msgcode"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			try {
				if(!isGson && dataM == null && !"0".equals(object.getString("msgcode"))) {
					Toast.makeText(context, object.getString("msg"), Toast.LENGTH_SHORT).show();
				}

				onFinally(object, object.getString("msgcode"), true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void doWork(T data, String code);

	public void onFinally(JSONObject obj, String code, boolean isNetSucceed){ } // 解析完成，如要执行操作，可重写该方法。

	@Override
	public void onFailed(int what, Response<String> response) {
		Log.i("onFailed", "请求失败：\n" + response.get());

		// Toast.makeText(context, "网络请求数据失败", Toast.LENGTH_SHORT).show();

		onFinally(new JSONObject(), "-1", false); // JSON数据请求失败
	}

}
