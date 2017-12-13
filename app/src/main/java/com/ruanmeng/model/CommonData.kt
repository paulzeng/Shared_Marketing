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

import java.io.Serializable
import java.util.*

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-21 17:28
 */
class CommonData {

    var msgcode: String? = null
    var msg: String? = null
    var data: List<CommonInfo> = ArrayList()

    class CommonInfo : Serializable {
        var id: String? = null
        var user_name: String? = null
        var mobile: String? = null
        var status1: String? = null
        var status2: String? = null
        var status3: String? = null
        var status4: String? = null
        var status5: String? = null

        var company_name: String? = null
        var company_addr: String? = null

        var success_amt: String? = null
        var commission: String? = null
        var create_time: String? = null

        var name: String? = null
        var cur_rec_user_id: String? = null
        var proj_name: String? = null
        var real_name: String? = null

        var image: String? = null
        var title: String? = null
        var link: String? = null

        var department_name: String? = null
        var cus_count: String? = null
        var icon: String? = null

        var user_id: String? = null
        var departure_place: String? = null
        var departure_lat: String? = null
        var departure_lng: String? = null
        var destination_place: String? = null
        var number_of_person: String? = null
        var pick_up_time: String? = null
        var destination_lng: String? = null
        var destination_lat: String? = null
        var logo: String = ""
        var o_status: String? = null

        var lid: String? = null
        var sn: String? = null
        var prize: String? = null
        var score: String? = null
        var exchange_time: String? = null
        var prize_date: String? = null
        var prize_addr: String? = null
        var exchange_status: String? = null
        var prize_name: String? = null
        var prize_img: String? = null
    }

}
