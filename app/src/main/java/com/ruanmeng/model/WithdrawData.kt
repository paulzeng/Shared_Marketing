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
package com.ruanmeng.model

import java.io.Serializable
import java.util.*

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-20 18:00
 */
class WithdrawData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : [{"id":"1","user_id":"1","amount":"120.00","bank":"上海浦东发展银行","account":"12345678944","opt_status":"1","create_time":"2017-02-10 20:19"}]
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: List<WithdrawInfo> = ArrayList()

    class WithdrawInfo : Serializable {
        /**
         * id : 1
         * user_id : 1
         * amount : 120.00
         * bank : 上海浦东发展银行
         * account : 12345678944
         * opt_status : 1
         * left_amt : 0
         * create_time : 2017-02-10 20:19
         */

        var id: String? = null
        var user_id: String? = null
        var amount: String? = null
        var bank: String? = null
        var account: String? = null
        var opt_status: String = "1"
        var create_time: String? = null
        var left_amt: String? = null

        var opening_bank: String? = null
        var customer_name: String? = null
        var customer_mobile: String? = null
        var success_amt: String = "0.00"
        var check_result: String = ""
    }
}
