/**
 * created by 小卷毛, 2016/11/22
 * Copyright (c) 2016, 416143467@qq.com All Rights Reserved.
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
package com.ruanmeng.share;

import com.ruanmeng.shared_marketing.BuildConfig;
import com.yolanda.nohttp.RequestMethod;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-17 16:34
 */
public class Const {

    final public static RequestMethod POST = RequestMethod.POST;
    final public static RequestMethod GET = RequestMethod.GET;

    public static long timeStamp = 1487381088L;

    public static boolean ISCODE = BuildConfig.LOG_DEBUG;
    public static String SYSTEM_PARAM;
    public static final String SAVE_FILE = "Shared_Marketing";
    public static String PREFERENCE_NAME = "TimeStamp";

    public static final String SALT = "wVHOPgfEUm4RVVdz";
    public static final String HASH = "fJn41RbkHY89JurR";

}
