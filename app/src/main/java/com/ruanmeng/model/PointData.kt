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
 * 创建时间：2017-02-19 10:01
 */
class PointData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : [{"id":"1","user_id":"8","score":"200","opt_type":"1","create_time":"2017-02-10 21:01"}]
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: List<PointInfo> = ArrayList()

    class PointInfo {
        /**
         * id : 1
         * user_id : 8
         * score : 200
         * opt_type : 1
         * create_time : 2017-02-10 21:01
         */

        var id: String? = null
        var user_id: String? = null
        var score: String? = null
        var opt_type: String = ""
        var create_time: String? = null

        var amount: String? = null

        var get_score: String? = null
        var month: String = ""
        var consume_score: String? = null
        var score_list: List<PointItem> = ArrayList()
    }

    class PointItem {
        var id: String = ""
        var user_id: String = ""
        var score: String = ""
        var opt_type: String = ""
        var score_type: String = ""
        var create_time: String = ""
        var create_month: String = ""
        var isShown: Boolean = false
        var position: Int = -1
    }
}
