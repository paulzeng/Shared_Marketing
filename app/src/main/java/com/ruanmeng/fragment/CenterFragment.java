package com.ruanmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ruanmeng.nohttp.CallServer;
import com.ruanmeng.nohttp.CustomHttpListener;
import com.ruanmeng.share.Const;
import com.ruanmeng.share.HttpIP;
import com.ruanmeng.shared_marketing.Partner.AppointActivity;
import com.ruanmeng.shared_marketing.Partner.CodeActivity;
import com.ruanmeng.shared_marketing.Partner.CommissionBalanceActivity;
import com.ruanmeng.shared_marketing.Partner.FeedbackActivity;
import com.ruanmeng.shared_marketing.Partner.InfoActivity;
import com.ruanmeng.shared_marketing.R;
import com.ruanmeng.utils.MD5Util;
import com.ruanmeng.utils.PreferencesUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CenterFragment extends Fragment {

    @BindView(R.id.iv_fragment_center_img)
    RoundedImageView iv_img;
    @BindView(R.id.tv_fragment_center_name)
    TextView tv_name;
    @BindView(R.id.tv_fragment_center_real)
    TextView tv_auth;
    @BindView(R.id.iv_fragment_center_vip)
    ImageView iv_vip;
    @BindView(R.id.iv_fragment_center_dian)
    ImageView iv_dian;
    @BindView(R.id.tv_fragment_center_type)
    TextView tv_type;
    @BindView(R.id.tv_fragment_center_yue)
    TextView tv_yue;
    @BindView(R.id.tv_fragment_center_yongjin)
    TextView tv_yongjin;
    @BindView(R.id.el_fragment_center_expand)
    ExpandableLayout expand;
    @BindView(R.id.stv_fragment_center_doing)
    SuperTextView tv_doing;
    @BindView(R.id.stv_fragment_center_done)
    SuperTextView tv_done;
    @BindView(R.id.stv_fragment_center_ji)
    SuperTextView tv_score;
    @BindView(R.id.fl_fragment_center_bg)
    FrameLayout fl_bg;

    private String mCount = "-1";

    //调用这个方法切换时不会释放掉Fragment
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_center, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String logo = PreferencesUtils.getString(getActivity(), "logo");
        if (!TextUtils.isEmpty(logo)) {
            Glide.with(getActivity())
                    .load(logo)
                    .placeholder(R.mipmap.personal_a1) // 等待时的图片
                    .error(R.mipmap.personal_a1) // 加载失败的图片
                    .crossFade()
                    .dontAnimate()
                    .into(iv_img);
            iv_img.setTag(R.id.image_tag, logo);
        }

        iv_vip.setVisibility(TextUtils.equals(PreferencesUtils.getString(getActivity(), "is_vip"), "1") ? View.GONE : View.VISIBLE);
        if (TextUtils.equals(PreferencesUtils.getString(getActivity(), "is_vip"), "1")) {
            expand.collapse();
        } else {
            expand.expand();
        }

        String userName = PreferencesUtils.getString(getActivity(), "user_name");
        String realName = PreferencesUtils.getString(getActivity(), "real_name");
        tv_name.setText(TextUtils.isEmpty(realName) ? userName : realName);
        tv_yue.setText(String.format("%.2f", Double.parseDouble(PreferencesUtils.getString(getActivity(), "account"))));
        tv_yongjin.setText(String.format("%.2f", Double.parseDouble(PreferencesUtils.getString(getActivity(), "commission"))));

        switch (PreferencesUtils.getString(getActivity(), "user_type")) {
            case "4":
                tv_type.setText("员工合伙人");
                break;
            case "5":
                tv_type.setText("业主合伙人");
                break;
            case "6":
                tv_type.setText("社会合伙人");
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        fl_bg.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        getData();

        getCountData();
    }

    private void getCountData() {
        Request<String> mRequest = NoHttp.createStringRequest(HttpIP.feedbackCount, Const.POST);
        mRequest.add("user_id", PreferencesUtils.getString(getActivity(), "user_id"));
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));

        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<JSONObject>(getActivity(), false, JSONObject.class) {
            @Override
            public void doWork(JSONObject data, String code) {
                /**
                 * {"msgcode":1,"msg":"\u64cd\u4f5c\u6210\u529f\uff01","data":{"feedback_count":"11","suggest_count":"3"}}
                 */
                try {
                    mCount = data.getJSONObject("data").getString("suggest_count");
                    String suggest_count = PreferencesUtils.getString(getActivity(), "suggest_count");

                    if (suggest_count == null
                            || Integer.parseInt(suggest_count) == -1) return;

                    if (Integer.parseInt(mCount) > Integer.parseInt(suggest_count))
                        iv_dian.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);

    }

    private void getData() {
        Request<String> mRequest = NoHttp.createStringRequest(HttpIP.userInfo, Const.POST);
        mRequest.add("user_id", PreferencesUtils.getString(getActivity(), "user_id"));
        mRequest.add("token", MD5Util.md5(Const.timeStamp));
        mRequest.add("time", String.valueOf(Const.timeStamp));
        // 添加到请求队列
        CallServer.getRequestInstance().add(getActivity(), mRequest,
                new CustomHttpListener<JSONObject>(getActivity(), false, JSONObject.class) {

                    @Override
                    public void doWork(JSONObject data, String code) {
                        /**
                         * {
                         *   "msgcode": 1,
                         *   "msg": "获取成功！",
                         *   "data": {
                         *      "id": "16",
                         *      "user_name": "13617066904",
                         *      "user_type": "6",
                         *      "pay_pass": "",
                         *      "mobile": "13617066904",
                         *      "gender": "1",
                         *      "province_id": "0",
                         *      "province_name": "",
                         *      "city_id": "0",
                         *      "city_name": "",
                         *      "account": "0.00",
                         *      "logo": "",
                         *      "id_card": "",
                         *      "real_name": "",
                         *      "can_withdraw": "",
                         *      "id_card_img1": "",
                         *      "id_card_img2": "",
                         *      "commission": "0.00",
                         *      "score": "200",
                         *      "is_vip": "1",
                         *      "unsettle_commission": 0,
                         *      "settled_commission": 0,
                         *      "is_virtual_acc": "",
                         *      "auth_info": {
                         *         "is_auth": 0,
                         *         "check_result": "审核中"
                         *      }
                         *   }
                         * }
                         */

                        try {
                            JSONObject obj = data.getJSONObject("data");
                            PreferencesUtils.putString(getActivity(), "user_name", obj.getString("user_name"));                        //昵称
                            PreferencesUtils.putString(getActivity(), "pay_pass", obj.getString("pay_pass"));                          //支付密码（加密后）
                            PreferencesUtils.putString(getActivity(), "gender", obj.getString("gender"));                              //性别
                            PreferencesUtils.putString(getActivity(), "logo", obj.getString("logo"));                                  //头像
                            PreferencesUtils.putString(getActivity(), "is_vip", obj.getString("is_vip"));                              //是否VIP 1：否，2：是
                            PreferencesUtils.putString(getActivity(), "can_withdraw", obj.getString("can_withdraw"));                  //可否提现
                            PreferencesUtils.putString(getActivity(), "real_name", obj.getString("real_name"));                        //真实姓名
                            PreferencesUtils.putString(getActivity(), "id_card", obj.getString("id_card"));                            //身份证号
                            PreferencesUtils.putString(getActivity(), "id_card_img1", obj.getString("id_card_img1"));                  //身份证正面
                            PreferencesUtils.putString(getActivity(), "id_card_img2", obj.getString("id_card_img2"));                  //身份证反面
                            PreferencesUtils.putString(getActivity(), "account", obj.getString("account"));                            //账户余额
                            PreferencesUtils.putString(getActivity(), "commission", obj.getString("commission"));                      //佣金
                            PreferencesUtils.putString(getActivity(), "score", obj.getString("score"));                                //积分
                            PreferencesUtils.putString(getActivity(), "unsettle_commission", obj.getString("unsettle_commission"));    //未结佣金
                            PreferencesUtils.putString(getActivity(), "settled_commission", obj.getString("settled_commission"));      //已结佣金
                            PreferencesUtils.putString(getActivity(), "is_virtual_acc", obj.getString("is_virtual_acc"));              //是否虚拟账户： 1：真实账户 2：虚拟账户
                            PreferencesUtils.putString(getActivity(), "is_auth", obj.getJSONObject("auth_info").getString("is_auth")); //认证状态： 0：未认证 1：审核中 2：已通过 3：已拒绝

                            PreferencesUtils.putString(getActivity(), "check_result", obj.getJSONObject("auth_info").getString("check_result"));  //审核结果
                            // PreferencesUtils.putString(getActivity(), "province_id", obj.getString("province_id"));                               //省份id
                            // PreferencesUtils.putString(getActivity(), "city_id", obj.getString("city_id"));                                       //城市id
                            // PreferencesUtils.putString(getActivity(), "city_name", obj.getString("city_name"));                                   //城市名称

                            String logo = obj.getString("logo");
                            if (iv_img.getTag(R.id.image_tag) == null) {
                                Glide.with(getActivity())
                                        .load(logo)
                                        .placeholder(R.mipmap.personal_a1) // 等待时的图片
                                        .error(R.mipmap.personal_a1) // 加载失败的图片
                                        .crossFade()
                                        .dontAnimate()
                                        .into(iv_img);
                                iv_img.setTag(R.id.image_tag, logo);
                            } else {
                                if (!TextUtils.equals(logo, (String) iv_img.getTag(R.id.image_tag))) {
                                    Glide.with(getActivity())
                                            .load(logo)
                                            .placeholder(R.mipmap.personal_a1) // 等待时的图片
                                            .error(R.mipmap.personal_a1) // 加载失败的图片
                                            .crossFade()
                                            .dontAnimate()
                                            .into(iv_img);
                                    iv_img.setTag(R.id.image_tag, logo);
                                }
                            }

                            iv_vip.setVisibility(TextUtils.equals(PreferencesUtils.getString(getActivity(), "is_vip"), "1") ? View.GONE : View.VISIBLE);
                            if (TextUtils.equals(PreferencesUtils.getString(getActivity(), "is_vip"), "1")) {
                                expand.collapse();
                            } else {
                                expand.expand();
                            }

                            String userName = PreferencesUtils.getString(getActivity(), "user_name");
                            String realName = PreferencesUtils.getString(getActivity(), "real_name");
                            tv_name.setText(TextUtils.isEmpty(realName) ? userName : realName);
                            tv_yue.setText(String.format("%.2f", Double.parseDouble(obj.getString("account"))));
                            tv_yongjin.setText(String.format("%.2f", Double.parseDouble(obj.getString("commission"))));
                            tv_doing.setRightString(String.format("%.2f", Double.parseDouble(obj.getString("unsettle_commission"))));
                            tv_done.setRightString(String.format("%.2f", Double.parseDouble(obj.getString("settled_commission"))));
                            tv_score.setRightString(obj.getString("score"));

                            switch (PreferencesUtils.getString(getActivity(), "is_auth")) {
                                case "0":
                                case "3":
                                    tv_auth.setText("（未认证）");
                                    break;
                                case "1":
                                    tv_auth.setText("（审核中）");
                                    break;
                                case "2":
                                    tv_auth.setText("（已认证）");
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, false);
    }

    @OnClick({ R.id.stv_fragment_center_doing,
               R.id.iv_fragment_center_sorrow,
               R.id.stv_fragment_center_done,
               R.id.stv_fragment_center_ji,
               R.id.stv_fragment_center_yue,
               R.id.stv_fragment_center_feedback})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.stv_fragment_center_doing:
                intent = new Intent(getActivity(), CommissionBalanceActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
            case R.id.stv_fragment_center_done:
                intent = new Intent(getActivity(), CommissionBalanceActivity.class);
                intent.putExtra("type", "2");
                startActivity(intent);
                break;
            case R.id.stv_fragment_center_ji:
                // intent = new Intent(getActivity(), PointsActivity.class);
                intent = new Intent(getActivity(), CodeActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_fragment_center_sorrow:
                intent = new Intent(getActivity(), InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.stv_fragment_center_yue:
                intent = new Intent(getActivity(), AppointActivity.class);
                startActivity(intent);
                break;
            case R.id.stv_fragment_center_feedback:
                PreferencesUtils.putString(getActivity(), "suggest_count", mCount);
                iv_dian.setVisibility(View.INVISIBLE);

                intent = new Intent(getActivity(), FeedbackActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
        }
    }
}
