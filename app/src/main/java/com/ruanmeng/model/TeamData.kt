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
 * 创建时间：2017-02-19 14:59
 */
class TeamData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : {"team_list":[{"id":"13","mobile":"13632939618","user_type":"5","user_name":"","real_name":"王春羽","rec_num":"0","success_num":"0","c_layer":"2","contribute_commissioin":"0"}],"team_count":[{"c_layer":"1","count":"1"},{"c_layer":"-2","count":"0"},{"c_layer":3,"count":1}],"superiors":{"id":"1","real_name":"杨松"}}
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: TeamInfo? = null

    class TeamInfo {
        /**
         * team_list : [{"id":"13","mobile":"13632939618","user_type":"5","user_name":"","real_name":"王春羽","rec_num":"0","success_num":"0","c_layer":"2","contribute_commissioin":"0"}]
         * team_count : [{"c_layer":"1","count":"1"},{"c_layer":"-2","count":"0"},{"c_layer":3,"count":1}]
         * superiors : {"id":"1","real_name":"杨松"}
         */

        var superiors: SuperiorsData? = null
        var team_list: List<TeamList> = ArrayList()
        var team_count: List<TeamCount> = ArrayList()
    }

    class SuperiorsData {
        /**
         * id : 1
         * real_name : 杨松
         */

        var id: String? = null
        var real_name: String? = null
    }

    class TeamList {
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

        var id: String? = null
        var mobile: String? = null
        var user_type: String? = null
        var user_name: String? = null
        var real_name: String? = null
        var rec_num: String? = null
        var success_num: String? = null
        var c_layer: String? = null
        var contribute_commissioin: String? = null
        var create_time: String? = null
    }

    class TeamCount {
        /**
         * c_layer : 1
         * count : 1
         */

        var c_layer: String? = null
        var count: String? = null
    }

}
