/**
 * created by 小卷毛, 2017/02/20
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.ruanmeng.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.ruanmeng.shared_marketing.R;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-20 19:37
 */
public class DialogHelper {

    public static void showDateDialog(
            final Context context,
            final String title,
            final String startDate,
            final String endDate,
            final DateCallBack callback) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_left, loop_right, loop_center;
            private int year_start, year_end, month_start, month_end, day_start, day_end;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_recommend_project, null);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_recommend_title);
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_recommend_cancle);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_dialog_recommend_ok);
                loop_left = (LoopView) view.findViewById(R.id.lv_dialog_recommend_left);
                loop_right = (LoopView) view.findViewById(R.id.lv_dialog_recommend_right);
                loop_center = (LoopView) view.findViewById(R.id.lv_dialog_recommend_center);

                tv_title.setText(title);
                loop_left.setTextSize(15f);
                loop_right.setTextSize(15f);
                loop_center.setTextSize(15f);
                loop_left.setNotLoop();
                loop_right.setNotLoop();
                loop_center.setNotLoop();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        int year = loop_left.getSelectedItem() + year_start;
                        int month = loop_center.getSelectedItem() + 1;
                        int day = loop_right.getSelectedItem() + 1;
                        if (year == year_start) {
                            month = loop_center.getSelectedItem() + month_start;
                        }
                        if (year == year_start && month == month_start) {
                            day = loop_right.getSelectedItem() + day_start;
                        }

                        String date_new = year + "-" + month + "-" + day;
                        if(month < 10 && day < 10)
                            date_new = year + "-0" + month + "-0" + day;
                        if(month < 10 && day >= 10)
                            date_new = year + "-0" + month + "-" + day;
                        if(month >= 10 && day < 10)
                            date_new = year + "-" + month + "-0" + day;

                        callback.doWork(year, month, day, date_new);
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                if (startDate == null || TextUtils.isEmpty(startDate)) {
                    throw new RuntimeException("The startDate can not be empty.");
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(TimeHelper.getInstance().strToDate(startDate));
                year_start = calendar.get(Calendar.YEAR);
                month_start = calendar.get(Calendar.MONTH) + 1;
                day_start = calendar.get(Calendar.DAY_OF_MONTH);

                if (endDate != null && !TextUtils.isEmpty(endDate)) {
                    calendar.setTime(TimeHelper.getInstance().strToDate(endDate));
                    year_end = calendar.get(Calendar.YEAR);
                    month_end = calendar.get(Calendar.MONTH) + 1;
                    day_end = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    year_end = Calendar.getInstance().get(Calendar.YEAR);
                    month_end = Calendar.getInstance().get(Calendar.MONTH) + 1;
                    day_end = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                }

                long time_start = TimeHelper.getInstance().stringToLong("yyyy-MM-dd", year_start + "-" + month_start + "-" + day_start);
                long time_end = TimeHelper.getInstance().stringToLong("yyyy-MM-dd", year_end + "-" + month_end + "-" + day_end);
                if (time_start > time_end) {
                    throw new RuntimeException("The time_start can not be larger than the time_end.");
                }

                String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
                String[] months_little = {"4", "6", "9", "11"};
                final List<String> list_big = Arrays.asList(months_big);
                final List<String> list_little = Arrays.asList(months_little);

                loop_left.setItems(dateToList(year_start, year_end, "%d年"));
                loop_center.setItems(dateToList(month_start, year_start == year_end ? month_end : 12, "%d月"));
                if (year_start == year_end && month_start == month_end) {
                    loop_right.setItems(dateToList(day_start, day_end, "%d日"));
                } else {
                    if (list_big.contains(String.valueOf(month_start))) {
                        loop_right.setItems(dateToList(day_start, 31, "%d日"));
                    } else if (list_little.contains(String.valueOf(month_start))) {
                        loop_right.setItems(dateToList(day_start, 30, "%d日"));
                    } else {
                        if ((year_start % 4 == 0 && year_start % 100 != 0) || year_start % 400 == 0)
                            loop_right.setItems(dateToList(day_start, 29, "%d日"));
                        else loop_right.setItems(dateToList(day_start, 28, "%d日"));
                    }
                }

                loop_center.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int year_num = loop_left.getSelectedItem() + year_start;
                        int month_num = Integer.parseInt(loop_center.getSelectedText().substring(0, loop_center.getSelectedText().length()-1));
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(month_num))) {
                            if (year_num == year_start && month_num == month_start) {
                                loop_right.setItems(dateToList(day_start, 31, "%d日"));
                            } else if (year_num == year_end && month_num == month_end) {
                                loop_right.setItems(dateToList(1, day_end, "%d日"));
                            } else {
                                loop_right.setItems(dateToList(1, 31, "%d日"));
                            }
                        } else if (list_little.contains(String.valueOf(month_num))) {
                            if (year_num == year_start && month_num == month_start) {
                                loop_right.setItems(dateToList(day_start, 30, "%d日"));

                                if (loop_right.getSelectedItem() > 30 - day_start) loop_right.setCurrentPosition(29 - day_start);
                            } else if (year_num == year_end && month_num == month_end) {
                                loop_right.setItems(dateToList(1, day_end, "%d日"));

                                if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                            } else {
                                loop_right.setItems(dateToList(1, 30, "%d日"));

                                if (loop_right.getSelectedItem() == 30) loop_right.setCurrentPosition(29);
                            }
                        } else {
                            if (((year_num) % 4 == 0 && (year_num) % 100 != 0) || (year_num) % 400 == 0) {
                                if (year_num == year_start && month_num == month_start) {
                                    loop_right.setItems(dateToList(day_start, 29, "%d日"));

                                    if (loop_right.getSelectedItem() > 29 - day_start) loop_right.setCurrentPosition(28 - day_start);
                                } else if (year_num == year_end && month_num == month_end) {
                                    loop_right.setItems(dateToList(1, day_end, "%d日"));

                                    if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                                } else {
                                    loop_right.setItems(dateToList(1, 29, "%d日"));

                                    if (loop_right.getSelectedItem() >= 29) loop_right.setCurrentPosition(28);
                                }
                            } else {
                                if (year_num == year_start && month_num == month_start) {
                                    loop_right.setItems(dateToList(day_start, 28, "%d日"));

                                    if (loop_right.getSelectedItem() > 28 - day_start) loop_right.setCurrentPosition(27 - day_start);
                                } else if (year_num == year_end && month_num == month_end) {
                                    loop_right.setItems(dateToList(1, day_end, "%d日"));

                                    if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                                } else {
                                    loop_right.setItems(dateToList(1, 28, "%d日"));

                                    if (loop_right.getSelectedItem() >= 28) loop_right.setCurrentPosition(27);
                                }
                            }
                        }
                    }
                });

                loop_left.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int year_num = index + year_start;
                        int month_num = Integer.parseInt(loop_center.getSelectedText().substring(0, loop_center.getSelectedText().length()-1));

                        if (year_num == year_start) {
                            loop_center.setItems(dateToList(month_start, 12, "%d月"));

                            if (loop_center.getSelectedItem() > 12 - month_start) loop_center.setCurrentPosition(11 - month_start);
                        } else if (year_num == year_end) {
                            loop_center.setItems(dateToList(1, month_end, "%d月"));

                            if (loop_center.getSelectedItem() >= month_end) loop_center.setCurrentPosition(month_end - 1);
                        } else {
                            loop_center.setItems(dateToList(1, 12, "%d月"));
                        }

                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(month_num))) {
                            if (year_num == year_start) {
                                if (month_num <= month_start) {
                                    loop_right.setItems(dateToList(day_start, 31, "%d日"));

                                    if (loop_right.getSelectedItem() > 31 - day_start) loop_right.setCurrentPosition(30 - day_start);
                                } else {
                                    loop_right.setItems(dateToList(1, 31, "%d日"));
                                }
                            } else if (year_num == year_end) {
                                if (month_num >= month_end) {
                                    loop_right.setItems(dateToList(1, day_end, "%d日"));

                                    if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                                } else {
                                    loop_right.setItems(dateToList(1, 31, "%d日"));
                                }
                            } else {
                                loop_right.setItems(dateToList(1, 31, "%d日"));
                            }
                        } else if (list_little.contains(String.valueOf(month_num))) {
                            if (year_num == year_start) {
                                if (month_num <= month_start) {
                                    loop_right.setItems(dateToList(day_start, 30, "%d日"));

                                    if (loop_right.getSelectedItem() > 29 - day_start) loop_right.setCurrentPosition(28 - day_start);
                                } else {
                                    loop_right.setItems(dateToList(1, 30, "%d日"));
                                }
                            } else if (year_num == year_end) {
                                if (month_num >= month_end) {
                                    loop_right.setItems(dateToList(1, day_end, "%d日"));

                                    if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                                } else {
                                    loop_right.setItems(dateToList(1, 30, "%d日"));
                                }
                            } else {
                                loop_right.setItems(dateToList(1, 30, "%d日"));
                            }
                        } else {
                            if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
                                if (year_num == year_start) {
                                    if (month_num <= month_start) {
                                        loop_right.setItems(dateToList(day_start, 29, "%d日"));

                                        if (loop_right.getSelectedItem() > 29 - day_start) loop_right.setCurrentPosition(28 - day_start);
                                    } else {
                                        loop_right.setItems(dateToList(1, 29, "%d日"));
                                    }
                                } else if (year_num == year_end) {
                                    if (month_num >= month_end) {
                                        loop_right.setItems(dateToList(1, day_end, "%d日"));

                                        if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                                    } else {
                                        loop_right.setItems(dateToList(1, 29, "%d日"));
                                    }
                                } else {
                                    loop_right.setItems(dateToList(1, 29, "%d日"));
                                }
                            } else {
                                if (year_num == year_start) {
                                    if (month_num <= month_start) {
                                        loop_right.setItems(dateToList(month_num <= month_start ? day_start : 1, 28, "%d日"));

                                        if (loop_right.getSelectedItem() > 28 - day_start) loop_right.setCurrentPosition(27 - day_start);
                                    } else {
                                        loop_right.setItems(dateToList(1, 28, "%d日"));

                                        if (loop_right.getSelectedItem() == 28) loop_right.setCurrentPosition(27);
                                    }
                                } else if (year_num == year_end) {
                                    if (month_num >= month_end) {
                                        loop_right.setItems(dateToList(1, day_end, "%d日"));

                                        if (loop_right.getSelectedItem() >= day_end) loop_right.setCurrentPosition(day_end - 1);
                                    } else {
                                        loop_right.setItems(dateToList(1, 28, "%d日"));

                                        if (loop_right.getSelectedItem() >= 28) loop_right.setCurrentPosition(27);
                                    }
                                } else {
                                    loop_right.setItems(dateToList(1, 28, "%d日"));

                                    if (loop_right.getSelectedItem() >= 28) loop_right.setCurrentPosition(27);
                                }
                            }
                        }
                    }
                });
            }

        };

        dialog.show();
    }

    public static void showDateDialog(
            final Context context,
            final int minValue,
            final int maxValue,
            final boolean isLimited,
            final DateCallBack callback) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_left, loop_right, loop_center;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_recommend_project, null);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_recommend_title);
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_recommend_cancle);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_dialog_recommend_ok);
                loop_left = (LoopView) view.findViewById(R.id.lv_dialog_recommend_left);
                loop_right = (LoopView) view.findViewById(R.id.lv_dialog_recommend_right);
                loop_center = (LoopView) view.findViewById(R.id.lv_dialog_recommend_center);

                tv_title.setText("选择时间");
                loop_left.setTextSize(15f);
                loop_right.setTextSize(15f);
                loop_center.setTextSize(15f);
                loop_left.setNotLoop();
                loop_right.setNotLoop();
                loop_center.setNotLoop();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        int year = loop_left.getSelectedItem() + minValue;
                        int month = loop_center.getSelectedItem() + 1;
                        int day = loop_right.getSelectedItem() + 1;

                        Calendar calendar = Calendar.getInstance();
                        int year_now = calendar.get(Calendar.YEAR);
                        int month_now = calendar.get(Calendar.MONTH);
                        int day_now = calendar.get(Calendar.DAY_OF_MONTH);

                        if (isLimited && year == year_now) {
                            if (month < month_now + 1) month = month_now + 1;
                            if (month == month_now + 1 && day < day_now) day = day_now;
                        }

                        String date_new = year + "-" + month + "-" + day;
                        if(month < 10 && day < 10)
                            date_new = year + "-0" + month + "-0" + day;
                        if(month < 10 && day >= 10)
                            date_new = year + "-0" + month + "-" + day;
                        if(month >= 10 && day < 10)
                            date_new = year + "-" + month + "-0" + day;

                        callback.doWork(year, month, day, date_new);
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loop_left.setItems(dateToList(minValue, maxValue, "%d年"));
                loop_center.setItems(dateToList(1, 12, "%d月"));
                loop_right.setItems(dateToList(1, 31, "%d日"));

                String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
                String[] months_little = {"4", "6", "9", "11"};
                final List<String> list_big = Arrays.asList(months_big);
                final List<String> list_little = Arrays.asList(months_little);

                loop_center.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int month_num = loop_center.getSelectedItem() + 1;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(month_num))) {
                            loop_right.setItems(dateToList(1, 31, "%d日"));
                        } else if (list_little.contains(String.valueOf(month_num))) {
                            loop_right.setItems(dateToList(1, 30, "%d日"));
                            if (loop_right.getSelectedItem() == 30) loop_right.setCurrentPosition(29);
                        } else {
                            if (((loop_left.getSelectedItem() + minValue) % 4 == 0
                                    && (loop_left.getSelectedItem() + minValue) % 100 != 0)
                                    || (loop_left.getSelectedItem() + minValue) % 400 == 0) {
                                loop_right.setItems(dateToList(1, 29, "%d日"));
                                if (loop_right.getSelectedItem() >= 29) loop_right.setCurrentPosition(28);
                            } else {
                                loop_right.setItems(dateToList(1, 28, "%d日"));
                                if (loop_right.getSelectedItem() >= 28) loop_right.setCurrentPosition(27);
                            }
                        }
                    }
                });

                loop_left.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int year_num = loop_left.getSelectedItem() + minValue;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(loop_center.getSelectedItem() + 1))) {
                            loop_right.setItems(dateToList(1, 31, "%d日"));
                        } else if (list_little.contains(String.valueOf(loop_center.getSelectedItem() + 1))) {
                            loop_right.setItems(dateToList(1, 30, "%d日"));
                        } else {
                            if ((year_num % 4 == 0 && year_num % 100 != 0)
                                    || year_num % 400 == 0)
                                loop_right.setItems(dateToList(1, 29, "%d日"));
                            else {
                                loop_right.setItems(dateToList(1, 28, "%d日"));
                                if (loop_right.getSelectedItem() == 28) loop_right.setCurrentPosition(27);
                            }
                        }
                    }
                });
            }

        };

        dialog.show();
    }

    public static void showDateDialog(
            final Context context,
            final int minValue,
            final int maxValue,
            final int count,
            final String title,
            final boolean isCurrentDate,
            final boolean isLimited,
            final DateAllCallBack callback) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_year, loop_month, loop_day, loop_hour, loop_minute;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_recommend_time, null);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_recommend_title);
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_recommend_cancle);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_dialog_recommend_ok);
                loop_year = (LoopView) view.findViewById(R.id.lv_dialog_recommend_year);
                loop_month = (LoopView) view.findViewById(R.id.lv_dialog_recommend_month);
                loop_day = (LoopView) view.findViewById(R.id.lv_dialog_recommend_day);
                loop_hour = (LoopView) view.findViewById(R.id.lv_dialog_recommend_hour);
                loop_minute = (LoopView) view.findViewById(R.id.lv_dialog_recommend_minute);

                tv_title.setText(title);
                loop_year.setTextSize(15f);
                loop_month.setTextSize(15f);
                loop_day.setTextSize(15f);
                loop_hour.setTextSize(15f);
                loop_minute.setTextSize(15f);
                loop_year.setNotLoop();
                loop_month.setNotLoop();
                loop_day.setNotLoop();
                loop_hour.setNotLoop();
                loop_minute.setNotLoop();

                switch (count) {
                    case 1:
                        loop_month.setVisibility(View.GONE);
                        loop_day.setVisibility(View.GONE);
                        loop_hour.setVisibility(View.GONE);
                        loop_minute.setVisibility(View.GONE);
                        break;
                    case 2:
                        loop_day.setVisibility(View.GONE);
                        loop_hour.setVisibility(View.GONE);
                        loop_minute.setVisibility(View.GONE);
                        break;
                    case 3:
                        loop_hour.setVisibility(View.GONE);
                        loop_minute.setVisibility(View.GONE);
                        break;
                    case 4:
                        loop_minute.setVisibility(View.GONE);
                        break;
                }

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        int year = loop_year.getSelectedItem() + minValue;
                        int month = loop_month.getSelectedItem() + 1;
                        int day = loop_day.getSelectedItem() + 1;
                        int hour = loop_hour.getSelectedItem();
                        int minute = loop_minute.getSelectedItem();

                        Calendar calendar = Calendar.getInstance();
                        int year_now = calendar.get(Calendar.YEAR);
                        int month_now = calendar.get(Calendar.MONTH);
                        int day_now = calendar.get(Calendar.DAY_OF_MONTH);

                        if (isLimited && year == year_now) {
                            if (month < month_now + 1) month = month_now + 1;
                            if (month == month_now + 1 && day < day_now) day = day_now;
                        }

                        String date_new;
                        switch (count) {
                            case 1:
                                date_new = year + "年";
                                break;
                            case 2:
                                date_new = year + "-" + month;
                                if(month < 10) date_new = year + "-0" + month;
                                break;
                            case 3:
                                date_new = year + "-" + month + "-" + day;
                                if(month < 10 && day < 10)
                                    date_new = year + "-0" + month + "-0" + day;
                                if(month < 10 && day >= 10)
                                    date_new = year + "-0" + month + "-" + day;
                                if(month >= 10 && day < 10)
                                    date_new = year + "-" + month + "-0" + day;
                                break;
                            case 4:
                                date_new = year + "-" + month + "-" + day + " " + hour + "时";
                                if(month < 10 && day < 10)
                                    date_new = year + "-0" + month + "-0" + day + " " + hour + "时";
                                if(month < 10 && day >= 10)
                                    date_new = year + "-0" + month + "-" + day + " " + hour + "时";
                                if(month >= 10 && day < 10)
                                    date_new = year + "-" + month + "-0" + day + " " + hour + "时";
                                break;
                            default:
                                date_new = year + "-" + month + "-" + day;
                                if(month < 10 && day < 10)
                                    date_new = year + "-0" + month + "-0" + day;
                                if(month < 10 && day >= 10)
                                    date_new = year + "-0" + month + "-" + day;
                                if(month >= 10 && day < 10)
                                    date_new = year + "-" + month + "-0" + day;

                                if(hour < 10 && minute < 10)
                                    date_new += " 0" + hour + ":0" + minute;
                                if(hour < 10 && minute >= 10)
                                    date_new += " 0" + hour + ":" + minute;
                                if(hour >= 10 && minute < 10)
                                    date_new += " " + hour + ":0" + minute;
                                if(hour >= 10 && minute >= 10)
                                    date_new += " " + hour + ":" + minute;
                                break;
                        }

                        callback.doWork(year, month, day, hour, minute, date_new);
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loop_year.setItems(dateToList(minValue, maxValue, "%d年"));
                loop_month.setItems(dateToList(1, 12, "%d月"));
                loop_day.setItems(dateToList(1, 31, "%d日"));
                loop_hour.setItems(dateToList(0, 23, "%d时"));
                loop_minute.setItems(dateToList(0, 59, "%d分"));

                if (isCurrentDate) {
                    loop_year.setCurrentPosition(Calendar.getInstance().get(Calendar.YEAR) - minValue);
                    loop_month.setCurrentPosition(Calendar.getInstance().get(Calendar.MONTH));
                    loop_day.setCurrentPosition(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
                }

                String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
                String[] months_little = {"4", "6", "9", "11"};
                final List<String> list_big = Arrays.asList(months_big);
                final List<String> list_little = Arrays.asList(months_little);

                loop_month.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int month_num = loop_month.getSelectedItem() + 1;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(month_num))) {
                            loop_day.setItems(dateToList(1, 31, "%d日"));
                        } else if (list_little.contains(String.valueOf(month_num))) {
                            loop_day.setItems(dateToList(1, 30, "%d日"));
                            if (loop_day.getSelectedItem() == 30) loop_day.setCurrentPosition(29);
                        } else {
                            if (((loop_year.getSelectedItem() + minValue) % 4 == 0
                                    && (loop_year.getSelectedItem() + minValue) % 100 != 0)
                                    || (loop_year.getSelectedItem() + minValue) % 400 == 0) {
                                loop_day.setItems(dateToList(1, 29, "%d日"));
                                if (loop_day.getSelectedItem() >= 29) loop_day.setCurrentPosition(28);
                            } else {
                                loop_day.setItems(dateToList(1, 28, "%d日"));
                                if (loop_day.getSelectedItem() >= 28) loop_day.setCurrentPosition(27);
                            }
                        }
                    }
                });

                loop_year.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int year_num = loop_year.getSelectedItem() + minValue;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(loop_month.getSelectedItem() + 1))) {
                            loop_day.setItems(dateToList(1, 31, "%d日"));
                        } else if (list_little.contains(String.valueOf(loop_month.getSelectedItem() + 1))) {
                            loop_day.setItems(dateToList(1, 30, "%d日"));
                        } else {
                            if ((year_num % 4 == 0 && year_num % 100 != 0)
                                    || year_num % 400 == 0)
                                loop_day.setItems(dateToList(1, 29, "%d日"));
                            else {
                                loop_day.setItems(dateToList(1, 28, "%d日"));
                                if (loop_day.getSelectedItem() == 28) loop_day.setCurrentPosition(27);
                            }
                        }
                    }
                });
            }

        };

        dialog.show();
    }

    public static void showCustomDialog(
            final Context context,
            final String title,
            final List<String> items,
            final boolean isLoop,
            final DialogCallBack callback) {

        final BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_left, loop_right, loop_center;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_recommend_project, null);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_recommend_title);
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_recommend_cancle);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_dialog_recommend_ok);
                loop_left = (LoopView) view.findViewById(R.id.lv_dialog_recommend_left);
                loop_right = (LoopView) view.findViewById(R.id.lv_dialog_recommend_right);
                loop_center = (LoopView) view.findViewById(R.id.lv_dialog_recommend_center);

                tv_title.setText(title);

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callback.doWork(loop_center.getSelectedItem(), items.get(loop_center.getSelectedItem()));
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loop_left.setVisibility(View.GONE);
                loop_right.setVisibility(View.GONE);
                loop_center.setTextSize(15f);
                if (!isLoop) loop_center.setNotLoop();

                loop_center.setItems(items);
            }
        };

        dialog.show();
    }

    public static void showCustomDialog(
            final Context context,
            final String title,
            final String right,
            final List<String> items,
            final boolean isLoop,
            final DialogCallBack callback) {

        final BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_left, loop_right, loop_center;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_recommend_project, null);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_recommend_title);
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_recommend_cancle);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_dialog_recommend_ok);
                loop_left = (LoopView) view.findViewById(R.id.lv_dialog_recommend_left);
                loop_right = (LoopView) view.findViewById(R.id.lv_dialog_recommend_right);
                loop_center = (LoopView) view.findViewById(R.id.lv_dialog_recommend_center);

                tv_title.setText(title);
                tv_ok.setText(right);

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callback.doWork(loop_center.getSelectedItem(), items.get(loop_center.getSelectedItem()));
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loop_left.setVisibility(View.GONE);
                loop_right.setVisibility(View.GONE);
                loop_center.setTextSize(15f);
                if (!isLoop) loop_center.setNotLoop();

                loop_center.setItems(items);
            }
        };

        dialog.show();
    }

    public static void showMultiDialog(
            final Context context,
            final String title,
            final List<List<String>> items,
            final boolean isLoop,
            final MultiDialogCallBack callback) {

        final BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_left, loop_right, loop_center;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_recommend_project, null);

                TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_recommend_title);
                TextView tv_cancel = (TextView) view.findViewById(R.id.tv_dialog_recommend_cancle);
                TextView tv_ok = (TextView) view.findViewById(R.id.tv_dialog_recommend_ok);
                loop_left = (LoopView) view.findViewById(R.id.lv_dialog_recommend_left);
                loop_right = (LoopView) view.findViewById(R.id.lv_dialog_recommend_right);
                loop_center = (LoopView) view.findViewById(R.id.lv_dialog_recommend_center);

                tv_title.setText(title);

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        List<Integer> potisions = new ArrayList<>();
                        List<String> names = new ArrayList<>();

                        switch (items.size()) {
                            case 1:
                                potisions.add(loop_left.getSelectedItem());

                                names.add(items.get(0).get(loop_left.getSelectedItem()));
                                break;
                            case 2:
                                potisions.add(loop_left.getSelectedItem());
                                potisions.add(loop_center.getSelectedItem());

                                names.add(items.get(0).get(loop_left.getSelectedItem()));
                                names.add(items.get(1).get(loop_center.getSelectedItem()));
                                break;
                            case 3:
                                potisions.add(loop_left.getSelectedItem());
                                potisions.add(loop_center.getSelectedItem());
                                potisions.add(loop_right.getSelectedItem());

                                names.add(items.get(0).get(loop_left.getSelectedItem()));
                                names.add(items.get(1).get(loop_center.getSelectedItem()));
                                names.add(items.get(2).get(loop_right.getSelectedItem()));
                                break;
                        }

                        callback.doWork(potisions, names);
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {

                loop_left.setTextSize(15f);
                loop_center.setTextSize(15f);
                loop_right.setTextSize(15f);
                if (!isLoop) {
                    loop_left.setNotLoop();
                    loop_center.setNotLoop();
                    loop_right.setNotLoop();
                }

                switch (items.size()) {
                    case 1:
                        loop_center.setVisibility(View.GONE);
                        loop_right.setVisibility(View.GONE);
                        loop_left.setItems(items.get(0));
                        break;
                    case 2:
                        loop_right.setVisibility(View.GONE);
                        loop_left.setItems(items.get(0));
                        loop_center.setItems(items.get(1));
                        break;
                    case 3:
                        loop_left.setItems(items.get(0));
                        loop_center.setItems(items.get(1));
                        loop_right.setItems(items.get(2));
                        break;
                }

            }
        };

        dialog.show();
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String btnText,
            final HintCallBack msgCallBack) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.content(content)
                .title(title)
                .btnText(btnText)
                .btnNum(1)
                .btnTextColor(context.getResources().getColor(R.color.blue))
                .showAnim(new BounceTopEnter())
                .show();
        materialDialog.setOnBtnClickL(
                new OnBtnClickL() { //left btn click listener
                    @Override
                    public void onBtnClick() {
                        materialDialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String left,
            final String right,
            final HintCallBack msgCallBack) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(content)
                .title(title)
                .btnText(left, right)
                .btnTextColor(
                        context.getResources().getColor(R.color.black),
                        context.getResources().getColor(R.color.blue))
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String left,
            final String right,
            boolean isOutDismiss,
            final HintCallBack msgCallBack) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(content)
                .title(title)
                .btnText(left, right)
                .btnTextColor(
                        context.getResources().getColor(R.color.black),
                        context.getResources().getColor(R.color.blue))
                .showAnim(new BounceTopEnter())
                .dismissAnim(new SlideBottomExit())
                .show();
        dialog.setCanceledOnTouchOutside(isOutDismiss);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    private static List<String> dateToList(int minValue, int maxValue, String format) {
        List<String> items = new ArrayList<>();

        for (int i = 0 ; i < maxValue - minValue + 1 ; i++) {
            int value = minValue + i;
            items.add(format != null ? String.format(format, value) : Integer.toString(value));
        }

        return items;
    }

    public interface DateCallBack {
        void doWork(int year, int month, int day, String date);
    }

    public interface DateAllCallBack {
        void doWork(int year, int month, int day, int hour, int minute, String date);
    }

    public interface DialogCallBack {
        void doWork(int postion, String name);
    }

    public interface MultiDialogCallBack {
        void doWork(List<Integer> potisions, List<String> names);
    }

    public interface HintCallBack {
        void doWork();
    }

}
