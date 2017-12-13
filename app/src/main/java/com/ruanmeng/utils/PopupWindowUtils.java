package com.ruanmeng.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ruanmeng.shared_marketing.R;

import java.util.List;

public class PopupWindowUtils {

    public static void showDatePopWindow(
            final Context context,
            View anchor,
            final List<String> items,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_house, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_pop_near_left);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);

        divider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        for (String item : items) {
            RadioButton rb = new RadioButton(context);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font15_gray);
            rb.setGravity(Gravity.CENTER_VERTICAL);
            rb.setPadding(DensityUtil.dp2px(10), 0, DensityUtil.dp2px(10), 0);
            Drawable rightDrawable = context.getResources().getDrawable(R.drawable.rb_popu_selector);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            rb.setCompoundDrawables(null, null, rightDrawable, null);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item);
            rb.setId(items.indexOf(item));
            rg.addView(rb);

            LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg.addView(v);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                callBack.doWork(checkedId, items.get(checkedId));
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public static void showDatePopWindow(
            final Context context,
            View anchor,
            int selected,
            final List<String> items,
            final PopupWindowCallBack callBack) {
        View view = LayoutInflater.from(context).inflate(R.layout.popu_layout_house, null);
        final PopupWindow popupWindow = new PopupWindow(
                view,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); // 适配华为p8
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_pop_near_left);
        View divider = view.findViewById(R.id.v_pop_divider);
        View divider_top = view.findViewById(R.id.v_pop_divider_top);

        divider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        for (String item : items) {
            RadioButton rb = new RadioButton(context);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dp2px(45));
            rb.setLayoutParams(params);
            rb.setTextAppearance(context, R.style.Font15_gray);
            rb.setGravity(Gravity.CENTER_VERTICAL);
            rb.setPadding(DensityUtil.dp2px(10), 0, DensityUtil.dp2px(10), 0);
            Drawable rightDrawable = context.getResources().getDrawable(R.drawable.rb_popu_selector);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            rb.setCompoundDrawables(null, null, rightDrawable, null);
            rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            rb.setText(item);
            rb.setId(items.indexOf(item));
            if (items.indexOf(item) == selected) rb.setChecked(true);
            rg.addView(rb);

            LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dp2px(0.5f));
            View v = new View(context);
            v.setLayoutParams(param);
            v.setBackgroundResource(R.color.divider);
            rg.addView(v);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupWindow.dismiss();
                callBack.doWork(checkedId, items.get(checkedId));
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                callBack.onDismiss();
            }
        });
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        //必须要有这句否则弹出popupWindow后监听不到Back键
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        //使其聚集
        popupWindow.setFocusable(true);
        //设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        //相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            divider_top.setVisibility(View.GONE);
            popupWindow.showAsDropDown(anchor);
        } else {
            divider_top.setVisibility(View.VISIBLE);
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
        }
        //刷新状态（必须刷新否则无效）
        popupWindow.update();
    }

    public interface PopupWindowCallBack {
        void doWork(int position, String name);
        void onDismiss();
    }

}
