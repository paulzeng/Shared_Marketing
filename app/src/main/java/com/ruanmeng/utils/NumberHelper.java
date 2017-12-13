package com.ruanmeng.utils;

import java.text.DecimalFormat;

public class NumberHelper {

    /**
     * 格式化数字为千分位显示；
     *
     * @param text 要格式化的数字；
     * @return
     */
    public static String fmtMicrometer(String text) {
        DecimalFormat df;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.");
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.0");
            } else {
                df = new DecimalFormat("###,##0.00");
            }
        } else {
            df = new DecimalFormat("###,##0");
        }
        double number;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }

}
