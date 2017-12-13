package com.ruanmeng.shared_marketing.Driver;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruanmeng.model.CommonData;
import com.ruanmeng.shared_marketing.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderSecondFragment extends Fragment {

    @BindView(R.id.tv_order_addr_start)
    TextView tv_start;
    @BindView(R.id.tv_order_addr_end)
    TextView tv_end;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_second, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonData.CommonInfo info = (CommonData.CommonInfo) getArguments().getSerializable("info");

        assert info != null;
        tv_start.setText(info.getDeparture_place());
        tv_end.setText(info.getDestination_place());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ll_order_addr)
    public void onViewClicked() { }
}
