/**
 * created by 小卷毛, 2017/02/19
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
package com.ruanmeng.model

import java.util.*

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-19 12:02
 */
class CommissionData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : [{"id":"1","user_id":"1","commission":"100.00","customer_level":"1","c_status":"1","commission_memo":"","create_time":"2017-02-04 16:38","mobile":"13632939606","real_name":"杨松","user_type":"5","rec_num":"0","success_num":"0"},{"id":"3","user_id":"1","commission":"600.00","customer_level":"1","c_status":"1","commission_memo":"","create_time":"2017-02-04 16:38","mobile":"13632939618","real_name":"王春羽","user_type":"5","rec_num":"","success_num":""}]
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: List<CommissionInfo> = ArrayList()

    class CommissionInfo {
        /**
         * id : 1
         * user_id : 1
         * commission : 100.00
         * customer_level : 1
         * c_status : 1
         * commission_memo :
         * create_time : 2017-02-04 16:38
         * mobile : 13632939606
         * real_name : 杨松
         * user_type : 5
         * rec_num : 0
         * success_num : 0
         */

        var id: String? = null
        var user_id: String? = null
        var commission: String? = null
        var customer_level: String? = null
        var c_status: String? = null
        var commission_memo: String? = null
        var create_time: String? = null
        var mobile: String? = null
        var real_name: String? = null
        var user_type: String? = null
        var rec_num: String? = null
        var success_num: String? = null

        var success_amt: String? = null
        var user_name: String? = null
        var customer_name: String? = null
        var customer_mobile: String? = null
    }

}
