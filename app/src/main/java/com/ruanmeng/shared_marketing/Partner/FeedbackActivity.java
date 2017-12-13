package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ruanmeng.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.FeedbackData;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.DialogHelper;
import com.yolanda.nohttp.NoHttp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.et_feedback_content)
    EditText et_content;
    @BindView(R.id.btn_feedback_ok)
    Button btn_ok;
    @BindView(R.id.lv_feedback_list)
    RecyclerView mRecyclerView;

    TextView tv_tel;

    private String mTel;
    private List<FeedbackData.FeedbackInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        init_title(TextUtils.equals("1", getIntent().getStringExtra("type")) ? "投诉和建议" : "意见反馈");

        getData(1);
    }

    @Override
    public void init_title() {
        super.init_title();
        View view = View.inflate(this, R.layout.header_feedback, null);
        tv_tel = (TextView) view.findViewById(R.id.tv_feedback_tel);

        try {
            JSONArray arr = new JSONObject(Const.SYSTEM_PARAM).getJSONArray("pa_complaint_tel");

            mTel = arr.getJSONObject(0).getString("value");
            tv_tel.setText("投诉电话：" + mTel);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        CommonAdapter mAdapter = new CommonAdapter<FeedbackData.FeedbackInfo>(this, R.layout.item_feedback_list, list) {
            @Override
            protected void convert(ViewHolder holder, FeedbackData.FeedbackInfo info, int position) {
                holder.setText(R.id.tv_item_feedback_content, info.getContent());
                RoundedImageView iv_img = holder.getView(R.id.iv_item_feedback_list);
                Glide.with(mContext)
                        .load(getString("logo"))
                        .placeholder(R.mipmap.personal_a20) // 等待时的图片
                        .error(R.mipmap.personal_a20) // 加载失败的图片
                        .crossFade()
                        .dontAnimate()
                        .into(iv_img);
                if (!TextUtils.isEmpty(info.getReply_content())) {
                    holder.getView(R.id.ll_item_feedback_reply).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tv_item_feedback_reply, info.getReply_content());
                } else {
                    holder.getView(R.id.ll_item_feedback_reply).setVisibility(View.GONE);
                }
            }
        };

        headerAndFooterAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        headerAndFooterAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(headerAndFooterAdapter);
    }

    @Override
    public void getData(final int pindex) {
        mRequest = NoHttp.createStringRequest(HttpIP.feedbackList, Const.POST);
        mRequest.add("user_id", getString("user_id"));
        mRequest.add("type", getIntent().getStringExtra("type"));
        mRequest.add("pindex", 1);

        getRequest(new CustomHttpListener<FeedbackData>(baseContext, true, FeedbackData.class) {
            @Override
            public void doWork(FeedbackData data, String code) {
                if (data.getData().size() > 0) {
                    list.clear();
                    list.addAll(data.getData());
                    int pos = headerAndFooterAdapter.getItemCount();
                    headerAndFooterAdapter.notifyItemRangeInserted(pos, list.size());

                    pos = linearLayoutManager.getItemCount() - 1;
                    if (pos >= 0) mRecyclerView.smoothScrollToPosition(pos);
                }
            }
        });
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.ll_feedback_tel:
                if (mTel == null) return;

                DialogHelper.showDialog(
                        this,
                        "拨打电话",
                        "投诉电话：" + mTel,
                        "取消",
                        "呼叫", new DialogHelper.HintCallBack() {
                            @Override
                            public void doWork() {
                                Intent phoneIntent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:" + mTel));
                                startActivity(phoneIntent);
                            }
                        });
                break;
            case R.id.btn_feedback_ok:
                final String content = et_content.getText().toString().trim();

                if (TextUtils.isEmpty(content)) {
                    showToask("请输入描述内容！");
                    return;
                }

                mRequest = NoHttp.createStringRequest(HttpIP.feedback, Const.POST);
                mRequest.add("user_id", getString("user_id"));
                mRequest.add("type", getIntent().getStringExtra("type"));
                mRequest.add("content", content);

                getRequest(new CustomHttpListener<JSONObject>(baseContext, false, null) {
                    @Override
                    public void doWork(JSONObject data, String code) {
                        list.add(new FeedbackData.FeedbackInfo(content, ""));
                        int pos = headerAndFooterAdapter.getItemCount();
                        headerAndFooterAdapter.notifyItemRangeInserted(pos, list.size());

                        pos = linearLayoutManager.getItemCount() - 1;
                        if (pos >= 0) mRecyclerView.smoothScrollToPosition(pos);

                        et_content.setText("");
                    }
                });
                break;
        }
    }

}
