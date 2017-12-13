/**
 * created by 小卷毛, 2017/02/24
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
 * 创建时间：2017-02-24 16:31
 */
class CompanyData {
    /**
     * msgcode : 1
     * msg : 获取成功！
     * data : {"rec_user":[{"id":"9","real_name":"","mobile":"13632939616"},{"id":"5","real_name":"","mobile":"13632939609"},{"id":"1","real_name":"灌篮高手","mobile":"13632939606"}],"all":"14","company_id":1,"company_name":"软盟","money":"466.00"}
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: CompanyInfo = CompanyInfo()

    class CompanyInfo {
        /**
         * rec_user : [{"id":"9","real_name":"","mobile":"13632939616"},{"id":"5","real_name":"","mobile":"13632939609"},{"id":"1","real_name":"灌篮高手","mobile":"13632939606"}]
         * all : 14
         * company_id : 1
         * company_name : 软盟
         * money : 466.00
         */

        var all: String? = null
        var company_id: String? = null
        var company_name: String? = null
        var money: String? = null
        var rec_user: List<UserItem> = ArrayList()

        var team_rec_count: String? = null
        var team_list: List<UserItem> = ArrayList()
    }

    class UserItem {
        /**
         * id : 9
         * real_name :
         * mobile : 13632939616
         * num:
         */

        var id: String? = null
        var mobile: String? = null
        var real_name: String? = null
        var num: String? = null

        var user_name: String? = null
        var rec_num: String? = null
        var success_num: String? = null
    }

}
