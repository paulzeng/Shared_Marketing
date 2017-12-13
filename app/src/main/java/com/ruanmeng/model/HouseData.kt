/**
 * created by 小卷毛, 2017/02/22
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
 * 创建时间：2017-02-22 20:32
 */
class HouseData {
    /**
     * msgcode : 1
     * msg : 获取成功！
     * data : [{"id":"4","proj_name":"招银大厦楼盘","district_name":"招银大厦","house_type_name":"高层","area_range":"40-199m2","yetai_name":"别墅、底层","basic_commission_rate":"22.00"},{"id":"6","proj_name":"招银大厦楼盘","district_name":"招银大厦","house_type_name":"高层","area_range":"40-199m2","yetai_name":"别墅、底层","basic_commission_rate":"22.00"}]
     */

    var msgcode: Int = 0
    var msg: String? = null
    var data: List<HouseInfo> = ArrayList()

    class HouseInfo {
        /**
         * id : 4
         * proj_name : 招银大厦楼盘
         * district_name : 招银大厦
         * images :
         * house_type_name : 高层
         * area_range : 40-199m2
         * yetai_name : 别墅、底层
         * basic_commission_rate : 22.00
         * house_price : 22.00
         */

        var id: String? = null
        var proj_name: String? = null
        var district_name: String? = null
        var images: String? = null
        var house_type_name: String? = null
        var area_range: String? = null
        var yetai_name: String? = null
        var basic_commission_rate: String? = null
        var house_price: String? = null
    }
}
