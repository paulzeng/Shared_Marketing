package com.ruanmeng.shared_marketing.Partner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.rollviewpager.OnItemClickListener;
import com.jude.rollviewpager.OnItemSelectListener;
import com.jude.rollviewpager.RollPagerView;
import com.ruanmeng.adapter.LoopAdapter;
import com.ruanmeng.base.BaseActivity;
import com.ruanmeng.model.BankItem;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.BitmapHelper;
import com.ruanmeng.utils.CommonUtil;
import com.ruanmeng.utils.DensityUtil;
import com.ruanmeng.view.WrapWebView;
import com.scrollablelayout.ScrollableLayout;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.ColorBar;
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

public class HouseDetailActivity extends BaseActivity {

    @BindView(R.id.tv_house_detail_title)
    TextView tv_title;
    @BindView(R.id.fl_house_detail_title)
    FrameLayout fl_title;
    @BindView(R.id.rp_house_detail_banner)
    RollPagerView mBannerRoll;
    @BindView(R.id.tv_house_detail_banner_hint)
    TextView tv_banner_hint;
    @BindView(R.id.tv_house_detail_banner)
    TextView tv_banner;
    @BindView(R.id.tv_house_detail_price)
    TextView tv_price;
    @BindView(R.id.tv_house_detail_status)
    TextView tv_status;
    @BindView(R.id.tv_house_detail_addr)
    TextView tv_addr;
    @BindView(R.id.tv_house_detail_yi)
    TextView tv_yi;
    @BindView(R.id.tv_house_detail_he)
    TextView tv_he;
    @BindView(R.id.tv_house_detail_done)
    TextView tv_done;
    @BindView(R.id.tv_house_detail_kai)
    TextView tv_kai;
    @BindView(R.id.tv_house_detail_jiao)
    TextView tv_jiao;
    @BindView(R.id.tv_house_detail_main)
    TextView tv_main;
    @BindView(R.id.lv_house_detail_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.fiv_house_detail_indicator)
    FixedIndicatorView fixedIndicatorView;
    @BindView(R.id.sl_house_detail_layout)
    ScrollableLayout sl_root;

    @BindView(R.id.wv_house_detail_mai)
    WrapWebView wv_mai;
    @BindView(R.id.iv_house_detail_map)
    ImageView iv_map;
    @BindView(R.id.tv_house_detail_kai1)
    TextView tv_kai1;
    @BindView(R.id.tv_house_detail_jiao1)
    TextView tv_jiao1;
    @BindView(R.id.wv_house_detail_rule)
    WrapWebView wv_rule;
    @BindView(R.id.tv_house_detail_shang)
    TextView tv_shang;
    @BindView(R.id.tv_house_detail_shangpin)
    TextView tv_shangpin;
    @BindView(R.id.tv_house_detail_wuye)
    TextView tv_wuye;
    @BindView(R.id.tv_house_detail_jianzhu)
    TextView tv_jianzhu;
    @BindView(R.id.tv_house_detail_zonghu)
    TextView tv_zonghu;
    @BindView(R.id.tv_house_detail_rongji)
    TextView tv_rongji;
    @BindView(R.id.tv_house_detail_lvhua)
    TextView tv_lvhua;
    @BindView(R.id.tv_house_detail_chewei)
    TextView tv_chewei;
    @BindView(R.id.tv_house_detail_junjia)
    TextView tv_junjia;
    @BindView(R.id.tv_house_detail_wuyefei)
    TextView tv_fee;
    @BindView(R.id.tv_house_detail_jianzhutype)
    TextView tv_type;
    @BindView(R.id.tv_house_detail_zhuangxiu)
    TextView tv_zhuangxiu;
    @BindView(R.id.tv_house_detail_chanquan)
    TextView tv_chanquan;
    @BindView(R.id.tv_house_detail_junjia1)
    TextView tv_junjia1;
    @BindView(R.id.tv_house_detail_wuyefei1)
    TextView tv_fee1;
    @BindView(R.id.tv_house_detail_jianzhutype1)
    TextView tv_type1;
    @BindView(R.id.tv_house_detail_zhuangxiu1)
    TextView tv_zhuangxiu1;
    @BindView(R.id.tv_house_detail_chanquan1)
    TextView tv_chanquan1;
    @BindView(R.id.ll_house_detail_xiang)
    LinearLayout ll_xiang;

    private String[] items = new String[]{"卖点", "详情"};
    private List<BankItem> list = new ArrayList<>();
    private List<String> titles = new ArrayList<>();      //标题
    private List<String> imgs = new ArrayList<>();        //全部
    private List<String> imgs_sync = new ArrayList<>();   //效果图
    private List<String> imgs_scene = new ArrayList<>();  //实景图
    private List<String> imgs_plan = new ArrayList<>();   //规划图
    private List<String> imgs_com = new ArrayList<>();    //配套图
    private List<String> imgs_sample = new ArrayList<>(); //样板图
    private List<String> imgs_sand = new ArrayList<>();   //沙盘图
    private LoopAdapter mLoopAdapter;
    private String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);
        ButterKnife.bind(this);
        transparentStatusBar(false);
        init_title();

        getData();
    }

    @Override
    public void init_title() {
        tv_title.setText(getIntent().getStringExtra("title"));
        setIndicator(fixedIndicatorView);
        ll_xiang.setVisibility(View.GONE);
        fl_title.getBackground().setAlpha(0);

        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new CommonAdapter<BankItem>(this, R.layout.item_house_detail_list, list) {
            @Override
            protected void convert(ViewHolder holder, final BankItem info, int position) {
                ImageView iv_img = holder.getView(R.id.iv_item_house_detail_img);
                holder.setText(R.id.tv_item_house_detail_name, info.getType_name() + "（" + info.getHouse_type() + "）");
                holder.setText(R.id.tv_item_house_detail_price, info.getPrice() + "起");
                holder.setText(R.id.tv_item_house_detail_type, info.getHall_type() + " " + info.getArea() + "㎡");
                holder.setText(R.id.tv_item_house_detail_status, TextUtils.equals("1", info.getSts()) ? "在售" : "停售");

                Glide.with(mContext)
                        .load(info.getImages())
                        .placeholder(R.mipmap.not_2) // 等待时的图片
                        .error(R.mipmap.not_2) // 加载失败的图片
                        .crossFade()
                        .into(iv_img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, HouseTypeActivity.class);
                        intent.putExtra("content", info);
                        startActivity(intent);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(adapter);

        sl_root.setTitleHeight(DensityUtil.dp2px(70));
        sl_root.setOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int translationY, int maxY) {
                int titleScrollHeight = fl_title.getBottom() - fl_title.getTop();
                int hearderHeight = mBannerRoll.getBottom() - mBannerRoll.getTop();

                if (translationY > (hearderHeight - titleScrollHeight)) {
                    fl_title.getBackground().setAlpha(255);
                } else {
                    int alpha = (int) (translationY * 1.0 / (hearderHeight - titleScrollHeight) *255);
                    fl_title.getBackground().setAlpha(alpha);
                }
            }
        });

        wv_mai.getSettings().setJavaScriptEnabled(true);
        wv_mai.getSettings().setJavaScriptEnabled(true); //设置WebView属性，能够执行Javascript脚本
        wv_mai.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //自动打开窗口
        wv_mai.getSettings().setLoadWithOverviewMode(true); //设置WebView可以加载更多格式页面
        // 设置出现缩放工具
        wv_mai.getSettings().setBuiltInZoomControls(true);
        wv_mai.getSettings().setDisplayZoomControls(false);

        wv_rule.getSettings().setJavaScriptEnabled(true);
        wv_rule.getSettings().setJavaScriptEnabled(true); //设置WebView属性，能够执行Javascript脚本
        wv_rule.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //自动打开窗口
        wv_rule.getSettings().setLoadWithOverviewMode(true); //设置WebView可以加载更多格式页面
        // 设置出现缩放工具
        wv_rule.getSettings().setBuiltInZoomControls(true);
        wv_rule.getSettings().setDisplayZoomControls(false);

        mLoopAdapter = new LoopAdapter(this, mBannerRoll);
        mBannerRoll.setAdapter(mLoopAdapter);
        mBannerRoll.setHintView(null);
        mBannerRoll.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(baseContext, PhotoActivity.class);
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_URLS, imgs.toArray(new String[imgs.size()]));
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_TITLES, titles.toArray(new String[titles.size()]));
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_INDEX, position);
                intent.putExtra(PhotoActivity.EXTRA_IMAGE_SAVE, false);
                startActivity(intent);
            }
        });
        mBannerRoll.setOnItemSelectListener(new OnItemSelectListener() {
            @Override
            public void onItemSelected(int position) {
                tv_banner.setText((position + 1) + "/" + imgs.size());
                tv_banner_hint.setText(imgs.get(position).substring(0, 3));
            }
        });
    }

    @Override
    public void getData() {
        mRequest = NoHttp.createStringRequest(HttpIP.buildingDetail, Const.POST);
        mRequest.add("proj_id", getIntent().getStringExtra("id"));

        getRequest(new CustomHttpListener<JSONObject>(this, true, null) {
            @Override
            public void doWork(JSONObject data, String code) {
                try {
                    JSONObject obj = data.getJSONObject("data");

                    lng = obj.getString("lng"); //经度
                    lat = obj.getString("lat"); //纬度

                    int width = CommonUtil.getScreenWidth(baseContext);
                    if(CommonUtil.getScreenWidth(baseContext) > 1024) width = 1024;

                    String str_map = HttpIP.staticimage +
                            "&center="+ lng +","+ lat +
                            "&width="+ String.valueOf(width) +
                            "&height="+ String.valueOf(BitmapHelper.dip2px(baseContext, 200)) +
                            "&markers="+ lng +","+ lat;

                    Glide.with(baseContext)
                            .load(str_map)
                            .placeholder(R.mipmap.not_2) // 等待时的图片
                            .error(R.mipmap.not_2) // 加载失败的图片
                            .crossFade()
                            .into(iv_map);

                    tv_price.setText(obj.getString("house_price"));
                    tv_status.setText(TextUtils.equals("1", obj.getString("sts")) ? "[在售]" : "[停售]");
                    tv_addr.setText(obj.getString("address"));
                    if (!obj.isNull("yixiang")) tv_yi.setText(obj.getString("yixiang"));
                    if (!obj.isNull("hezuo")) tv_he.setText(obj.getString("hezuo"));
                    if (!obj.isNull("chengjiao")) tv_done.setText(obj.getString("chengjiao"));
                    if (!obj.isNull("start_time")) tv_kai.setText(obj.getString("start_time"));
                    if (!obj.isNull("live_time")) tv_jiao.setText(obj.getString("live_time"));
                    if (!obj.isNull("cooperation_rule")) {
                        String str = "<meta " +
                                "name=\"viewport\" " +
                                "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                "<style>" +
                                ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                "img{ max-width: 100% !important;display:block;height:auto !important;}" +
                                "*{ max-width:100% !important;}\n" +
                                "</style>";
                        String cooperation_rule = obj.getString("cooperation_rule");
                        wv_rule.loadDataWithBaseURL(HttpIP.IP, str + "<div class=\"con\">" + cooperation_rule + "</div>", "text/html", "utf-8", "");
                    }

                    tv_kai1.setText(obj.getString("start_time"));
                    tv_jiao1.setText(obj.getString("live_time"));
                    tv_shang.setText(obj.getString("developer"));
                    tv_shangpin.setText(obj.getString("developer_brand"));
                    tv_wuye.setText(obj.getString("prop_mag_com"));
                    tv_jianzhu.setText(obj.getString("build_area") + "㎡");
                    tv_zonghu.setText(obj.getString("household_number") + "户");
                    tv_rongji.setText(obj.getString("plot_ratio"));
                    tv_lvhua.setText(obj.getString("greening_rate") + "%");
                    tv_chewei.setText(obj.getString("parking_num"));

                    tv_junjia.setText(obj.getString("house_price") + "元/㎡");
                    tv_fee.setText(obj.getString("house_prop_mag_fee") + "元/㎡/月");
                    tv_type.setText(obj.getString("house_building_type"));
                    tv_zhuangxiu.setText(obj.getString("house_decoration"));
                    tv_chanquan.setText(obj.getString("house_property_right_years") + "年");

                    tv_junjia1.setText(obj.getString("office_price") + "元/㎡");
                    tv_fee1.setText(obj.getString("office_prop_mag_fee") + "元/㎡/月");
                    tv_type1.setText(obj.getString("office_building_type"));
                    tv_zhuangxiu1.setText(obj.getString("office_decoration"));
                    tv_chanquan1.setText(obj.getString("office_property_right_years") + "年");

                    String str = "<meta " +
                            "name=\"viewport\" " +
                            "content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                            "<style>" +
                            ".con{ width:100%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em;}\n" +
                            ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                            "img{ max-width: 100% !important;display:block;height:auto !important;}" +
                            "*{ max-width:100% !important;}\n" +
                            "</style>";
                    String summary = obj.getString("summary");
                    wv_mai.loadDataWithBaseURL(HttpIP.IP, str + "<div class=\"con\">" + summary + "</div>", "text/html", "utf-8", "");

                    JSONArray arr_type = obj.getJSONArray("house_type");
                    for (int i = 0 ; i < arr_type.length() ; i++) {
                        JSONObject object = arr_type.getJSONObject(i);
                        ArrayList<String> images_more = new ArrayList<>();
                        for (int j = 0 ; j < object.getJSONArray("images_more").length() ; j++) {
                            images_more.add(object.getJSONArray("images_more").getString(j));
                        }
                        list.add(new BankItem(
                                object.getString("type_name"),
                                object.getString("house_type"),
                                object.getString("area"),
                                object.getString("price"),
                                object.getString("images"),
                                object.getString("sts"),
                                object.getString("hall_type"),
                                object.getString("details"),
                                images_more));
                    }
                    tv_main.setText("主力户型（" + list.size() + "个）");
                    adapter.notifyDataSetChanged();

                    if (!obj.isNull("imagess")) {
                        JSONArray arr = obj.getJSONArray("imagess");
                        for (int i = 0 ; i < arr.length() ; i++) {
                            imgs_sync.add("效果图" + arr.getString(i));
                        }
                        imgs.addAll(imgs_sync);
                    }
                    if (!obj.isNull("outdorr_scene_imgs")) {
                        JSONArray arr = obj.getJSONArray("outdorr_scene_imgs");
                        for (int i = 0 ; i < arr.length() ; i++) {
                            imgs_scene.add("实景图" + arr.getString(i));
                        }
                        imgs.addAll(imgs_scene);
                    }
                    if (!obj.isNull("plan_imgs")) {
                        JSONArray arr = obj.getJSONArray("plan_imgs");
                        for (int i = 0 ; i < arr.length() ; i++) {
                            imgs_plan.add("规划图" + arr.getString(i));
                        }
                        imgs.addAll(imgs_plan);
                    }
                    if (!obj.isNull("complement_imgs")) {
                        JSONArray arr = obj.getJSONArray("complement_imgs");
                        for (int i = 0 ; i < arr.length() ; i++) {
                            imgs_com.add("配套图" + arr.getString(i));
                        }
                        imgs.addAll(imgs_com);
                    }
                    if (!obj.isNull("sample_imgs")) {
                        JSONArray arr = obj.getJSONArray("sample_imgs");
                        for (int i = 0 ; i < arr.length() ; i++) {
                            imgs_sample.add("样板图" + arr.getString(i));
                        }
                        imgs.addAll(imgs_sample);
                    }
                    if (!obj.isNull("sandtable_imgs")) {
                        JSONArray arr = obj.getJSONArray("sandtable_imgs");
                        for (int i = 0 ; i < arr.length() ; i++) {
                            imgs_sand.add("沙盘图" + arr.getString(i));
                        }
                        imgs.addAll(imgs_sand);
                    }

                    if (imgs_sync.size() > 0) titles.add("效果图");
                    if (imgs_scene.size() > 0) titles.add("实景图");
                    if (imgs_plan.size() > 0) titles.add("规划图");
                    if (imgs_com.size() > 0) titles.add("配套图");
                    if (imgs_sample.size() > 0) titles.add("样板图");
                    if (imgs_sand.size() > 0) titles.add("沙盘图");

                    tv_banner.setText("1/" + imgs.size());
                    tv_banner_hint.setText(imgs.get(0).substring(0, 3));
                    mLoopAdapter.setImgs(imgs);
                    if (imgs.size() <= 1) mBannerRoll.pause();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setIndicator(Indicator indicator) {
        indicator.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_tab_top, parent, false);
                }
                TextView textView = (TextView) convertView;
                //用了固定宽度可以避免TextView文字大小变化，tab宽度变化导致tab抖动现象
                textView.setWidth(DensityUtil.dp2px(50));
                textView.setText(items[position]);
                return convertView;
            }
        });

        indicator.setScrollBar(new ColorBar(this, getResources().getColor(R.color.colorAccent), 5));
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                switch (select) {
                    case 0:
                        wv_mai.setVisibility(View.VISIBLE);
                        ll_xiang.setVisibility(View.GONE);
                        break;
                    case 1:
                        wv_mai.setVisibility(View.GONE);
                        ll_xiang.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        indicator.setCurrentItem(0, true);
    }

    @Override
    public void doClick(View v) {
        super.doClick(v);
        switch (v.getId()) {
            case R.id.tv_house_detail_recommend:
                Intent intent = getIntent();
                intent.setClass(this, RecommendActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_house_detail_addr:
                // TODO: 2017/3/28 0028 地图功能
                break;
        }
    }
}
