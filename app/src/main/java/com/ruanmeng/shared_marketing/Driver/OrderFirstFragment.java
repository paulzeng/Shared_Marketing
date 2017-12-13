package com.ruanmeng.shared_marketing.Driver;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ruanmeng.model.CommonData;
import com.ruanmeng.shared_marketing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFirstFragment extends Fragment {


    @BindView(R.id.iv_order_img)
    RoundedImageView iv_img;
    @BindView(R.id.tv_order_name1)
    TextView tv_name1;
    @BindView(R.id.tv_order_addr1)
    TextView tv_addr1;
    @BindView(R.id.tv_order_addr2)
    TextView tv_addr2;
    @BindView(R.id.tv_order_name2)
    TextView tv_name2;
    @BindView(R.id.tv_order_tel)
    TextView tv_tel;
    @BindView(R.id.tv_order_num)
    TextView tv_num;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_first, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonData.CommonInfo info = (CommonData.CommonInfo) getArguments().getSerializable("info");

        assert info != null;
        Glide.with(getActivity())
                .load(info.getLogo())
                .placeholder(R.mipmap.personal_a20) // 等待时的图片
                .error(R.mipmap.personal_a20) // 加载失败的图片
                .crossFade()
                .dontAnimate()
                .into(iv_img);

        tv_name1.setText("申请人：" + info.getName());
        tv_addr1.setText(info.getDeparture_place());
        tv_addr2.setText(info.getDestination_place());
        tv_name2.setText(info.getName());
        tv_tel.setText(info.getMobile());
        tv_num.setText(info.getNumber_of_person() + "人");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.iv_order_card)
    public void onViewClicked() { }
}
