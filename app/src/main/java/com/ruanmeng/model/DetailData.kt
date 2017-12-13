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
 * 创建时间：2017-02-21 20:26
 */
class DetailData {
    /**
     * msgcode : 1
     * msg : 获取成功！
     * data : {"name":"张三","mobile":"13985654152","create_time":"0","cus_status":"4","rec_name":"灌篮高手","rec_mobile":"13632939606","saler_name":"灌篮高手","saler_mobile":"13632939606","project":"碧沙岗楼盘","record":[{"id":"1","user_id":"1","customer_id":"0","cur_status":"1","follow_memo":"啥事memo","create_time":"123"}]}
     */

    var msgcode: Int = 0
    var msg: String? = null
    var data: DetailInfo = DetailInfo()

    class DetailInfo {
        /**
         * name : 张三
         * mobile : 13985654152
         * create_time : 0
         * cus_status : 4
         * rec_name : 灌篮高手
         * rec_mobile : 13632939606
         * saler_name : 灌篮高手
         * saler_mobile : 13632939606
         * project : 碧沙岗楼盘
         * house_type :
         * customer_location :
         * yetai :
         * Id_card_no :
         * memo :
         * dept_name :
         * record : [{"id":"1","user_id":"1","customer_id":"0","cur_status":"1","follow_memo":"啥事memo","create_time":"123"}]
         */

        var name: String? = null
        var mobile: String? = null
        var create_time: String? = null
        var cus_status: Int = 0
        var rec_name: String? = null
        var rec_mobile: String? = null
        var saler_name: String? = null
        var saler_mobile: String? = null
        var project: String? = null
        var house_type: String? = null
        var customer_location: String? = null
        var yetai: String? = null
        var Id_card_no: String? = null
        var memo: String? = null
        var dept_name: String? = null
        var proj_type: String? = null
        var record: List<RecordInfo>  = ArrayList()
    }

    class RecordInfo {
        /**
         * id : 1
         * user_id : 1
         * customer_id : 0
         * cur_status : 1
         * follow_memo : 啥事memo
         * create_time : 123
         */

        var id: String? = null
        var user_id: String? = null
        var customer_id: String? = null
        var cur_status: String? = null
        var follow_memo: String? = null
        var create_time: String? = null
    }

}
