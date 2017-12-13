package com.ruanmeng.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.dialog.nohttp.CallServer;
import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.rest.Request;
import com.zhy.adapter.recyclerview.CommonAdapter;

public class BaseFragment extends Fragment implements
        TextWatcher,
        View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        CompoundButton.OnCheckedChangeListener {

    /**
     * 网络请求对象.
     */
    public Request<String> mRequest;
    /**
     * 下载请求对象.
     */
    public DownloadRequest mDownloadRequest;
    /**
     * 上下文context
     */
    public Activity baseContext;
    /**
     * RecyclerView数据管理的LayoutManager
     */
    public LinearLayoutManager linearLayoutManager;
    public GridLayoutManager gridLayoutManager;
    public StaggeredGridLayoutManager staggeredGridLayoutManager;
    /**
     * CommonAdapter的adapter
     */
    public CommonAdapter adapter;
    /**
     * RecyclerView增加header的adapter
     */
    public HeaderAndFooterRecyclerViewAdapter headerAndFooterAdapter;
    /**
     * 分页加载页数
     */
    public int pageNum = 1;
    /**
     * 是否正在上拉加载中
     */
    public boolean isLoadingMore;

    //网络数据请求方法
    public void getData() { }

    public void getData(int pindex) { }

    public void getData(int pindex, boolean isLoading) { }

    //Nohttp请求
    public <T> void getRequest(CustomHttpListener<T> customHttpListener) {
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));
        CallServer.getRequestInstance().add(getActivity(), mRequest, customHttpListener, true);
    }

    //Nohttp网络请求
    public <T> void getRequest(CustomHttpListener<T> customHttpListener, boolean isLoading) {
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));
        CallServer.getRequestInstance().add(getActivity(), mRequest, customHttpListener, isLoading);
    }

    public void showToask(String tip) {
        CommonUtil.showToask(getActivity(), tip);
    }

    public String getString(String key) {
        return PreferencesUtils.getString(getActivity(), key, "");
    }

    public String getString(String key, String defaultValue) {
        return PreferencesUtils.getString(getActivity(), key, defaultValue);
    }

    public void putString(String key, String vaule) {
        PreferencesUtils.putString(getActivity(), key, vaule);
    }

    /**
     * 切换Activity
     *
     * @param activity 需要切换到的Activity
     */
    public void startActivity(Class<?> activity) {
        Intent intent = new Intent(getActivity(), activity);
        this.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出Activity时取消网络请求
        if (mRequest != null) mRequest.cancel();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void onClick(View v) { }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { }

}
