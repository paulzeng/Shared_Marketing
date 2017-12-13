package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.shared_marketing.WebActivity;
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

public class HelpActivity extends BaseActivity {

    @BindView(R.id.lv_help_list)
    RecyclerView mRecyclerView;

    private List<BankItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        init_title("帮助中心");

        getData();
    }

    @Override
    public void init_title() {
        super.init_title();

        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<BankItem>(this, R.layout.item_help_list, list) {
            @Override
            protected void convert(ViewHolder holder, final BankItem info, int position) {

                holder.setText(R.id.tv_item_help_title, info.getTitle());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent(baseContext, WebActivity.class);
                            intent.putExtra("title", info.getTitle());
                            intent.putExtra("content", info.getContent());
                            intent.putExtra("create_time", info.getCreate_time());
                            intent.putExtra("type", "2");
                            startActivity(intent);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.contentShow, Const.POST);
        mRequest.add("type", "3");
        mRequest.add("role_type", getString("user_type"));

        getRequest(new CustomHttpListener<JSONObject>(this, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    JSONArray arr = data.getJSONArray("data");

                    for (int i = 0 ; i < arr.length() ; i++) {
                        JSONObject object = arr.getJSONObject(i);
                        list.add(new BankItem(
                                object.getString("title"),
                                object.getString("content"),
                                object.getString("create_time")));
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
