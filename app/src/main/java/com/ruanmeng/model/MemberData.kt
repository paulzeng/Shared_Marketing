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
 * 创建时间：2017-02-21 15:46
 */
class MemberData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : {"team_member_info":{"id":"12","mobile":"13632939617","real_name":"王冬","rec_num":"0","success_num":"0","create_time":"2017-02-11 14:23"},"total_commission":"253.00","commission_List":[{"id":"2","user_id":"1","success_amt":"0.00","commission":"253.00","customer_id":"2","rec_user_id":"12","create_time":"2017-02-04 16:38","customer_name":"李四","house_name":"开源壹号三室一厅","proj_name":"开元壹号"}]}
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: MemberModel? = null

    class MemberModel {
        /**
         * team_member_info : {"id":"12","mobile":"13632939617","real_name":"王冬","rec_num":"0","success_num":"0","create_time":"2017-02-11 14:23"}
         * total_commission : 253.00
         * commission_List : [{"id":"2","user_id":"1","success_amt":"0.00","commission":"253.00","customer_id":"2","rec_user_id":"12","create_time":"2017-02-04 16:38","customer_name":"李四","house_name":"开源壹号三室一厅","proj_name":"开元壹号"}]
         */

        var team_member_info: MemberInfo? = null
        var total_commission: String = "0.00"
        var commission_List: List<MemberList> = ArrayList()
        var commission_detail: List<MemberList> = ArrayList()
    }

    class MemberInfo {
        /**
         * id : 12
         * mobile : 13632939617
         * real_name : 王冬
         * rec_num : 0
         * success_num : 0
         * create_time : 2017-02-11 14:23
         */

        var id: String? = null
        var mobile: String? = null
        var real_name: String? = null
        var rec_num: String? = null
        var success_num: String? = null
        var create_time: String? = null
    }

    class MemberList {
        /**
         * id : 2
         * user_id : 1
         * success_amt : 0.00
         * commission : 253.00
         * customer_id : 2
         * rec_user_id : 12
         * create_time : 2017-02-04 16:38
         * customer_name : 李四
         * saler_user_id: 5
         * proj_id: 1
         * house_area: 1
         * house_name : 开源壹号三室一厅
         * proj_name : 开元壹号
         * saler_name : 开元壹号
         * commission_rate :
         */

        var id: String? = null
        var user_id: String? = null
        var success_amt: String = "0.00"
        var commission: String? = null
        var customer_id: String? = null
        var rec_user_id: String? = null
        var create_time: String? = null
        var customer_name: String? = null
        var saler_user_id: String? = null
        var proj_id: String? = null
        var house_area: String = "0"
        var house_name: String? = null
        var proj_name: String? = null
        var saler_name: String? = null
        var commission_rate: Double = 0.00
    }

}
