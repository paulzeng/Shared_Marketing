package com.ruanmeng.model

import java.util.ArrayList

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-03-29 10:08
 */

class GrabData {
    var msgcode: String? = null
    var msg: String? = null
    var data: GrabInfo = GrabInfo()

    class GrabInfo {
        var num: Int = 0
        var data: List<CommonInfo> = ArrayList()
    }

    class CommonInfo {
        var id: String? = null
        var user_name: String? = null
        var mobile: String? = null
        var status1: String? = null
        var status2: String? = null
        var status3: String? = null
        var status4: String? = null
        var status5: String? = null

        var company_name: String? = null

        var success_amt: String? = null
        var commission: String? = null
        var create_time: String? = null

        var name: String? = null
        var cur_rec_user_id: String? = null
        var proj_name: String? = null
        var real_name: String? = null
    }
}
