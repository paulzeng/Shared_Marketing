/**
 * created by 小卷毛, 2017/02/21
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
 * 创建时间：2017-02-21 17:28
 */
class CustomerData {
    /**
     * msgcode : 1
     * msg : 获取成功！
     * data : {"count_list":[{"cus_status":"1","num":"1"},{"cus_status":"2","num":"1"},{"cus_status":"3","num":"1"},{"cus_status":"4","num":"2"},{"cus_status":"6","num":"1"}],"customer_list":[{"id":"14","saler_user_id":"1","name":"张三","mobile":"13985654152","proj_id":"1","create_time":"0","cus_status":"1"}]}
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: CustomerInfo = CustomerInfo()

    class CustomerInfo {
        /**
         * count_list : [{"cus_status":"1","num":"1"},{"cus_status":"2","num":"1"},{"cus_status":"3","num":"1"},{"cus_status":"4","num":"2"},{"cus_status":"6","num":"1"}]
         * customer_list : [{"id":"14","saler_user_id":"1","name":"张三","mobile":"13985654152","proj_id":"1","create_time":"0","cus_status":"1"}]
         */

        var count_list: List<CountList> = ArrayList()
        var customer_list: List<CustomerList> = ArrayList()

        var name: String? = null
        var information: List<CustomerList> = ArrayList()

        var list: List<CountList> = ArrayList()
        var user: List<CustomerList> = ArrayList()
    }

    class CountList {
        /**
         * cus_status : 1
         * num : 1
         */

        var cus_status: String? = null
        var num: String? = null

        constructor(cus_status: String, num: String) {
            this.cus_status = cus_status
            this.num = num
        }
    }

    class CustomerList {
        /**
         * id : 14
         * saler_user_id : 1
         * saler :
         * name : 张三
         * mobile : 13985654152
         * proj_id : 1
         * project :
         * create_time : 0
         * cus_status : 1
         * proj_type : 1
         */

        var id: String? = null
        var saler_user_id: String? = null
        var saler: String? = null
        var name: String? = null
        var mobile: String? = null
        var proj_id: String? = null
        var project: String? = null
        var create_time: String? = null
        var cus_status: Int = 0
        var proj_type: String? = null

        /**
         * id : 13
         * mobile : 13632939618
         * user_type : 5
         * user_name :
         * real_name : 王春羽
         * rec_num : 0
         * success_num : 0
         * c_layer : 2
         * contribute_commissioin : 0
         */

        var user_type: String? = null
        var user_name: String? = null
        var real_name: String? = null
        var rec_num: String? = null
        var success_num: String? = null
        var c_layer: String? = null
        var contribute_commissioin: String? = null

        var proj_name: String? = null

        /**
         * 抢单列表
         */
        var status1: String? = null
        var status2: String? = null
        var status3: String? = null
        var status4: String? = null
        var status5: String? = null
        var company_name: String? = null
        var success_amt: String? = null
        var commission: String? = null
        var cur_rec_user_id: String? = null
    }

}
