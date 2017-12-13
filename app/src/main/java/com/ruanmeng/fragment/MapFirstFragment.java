package com.ruanmeng.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruanmeng.shared_marketing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFirstFragment extends Fragment {


    @BindView(R.id.tv_nav_title)
    TextView tv_title;
    @BindView(R.id.tv_fragment_first_city)
    TextView tv_city;
    @BindView(R.id.tv_fragment_first_addr)
    TextView tv_addr;
    Unbinder unbinder;
    @BindView(R.id.ll_fragment_first_addr)
    LinearLayout llFragmentFirstAddr;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_first, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        tv_title.setText("一键约车");
    }

    public void setCity(String city) {
        tv_city.setText(city);
    }

    public void setAddress(String addr) {
        tv_addr.setText(addr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.layout_title)
    public void onViewClicked() { }
}
