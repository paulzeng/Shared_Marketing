package com.ruanmeng.model

import java.util.ArrayList

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-03-29 10:08
 */

class GrabSearchData {
    var msgcode: String? = null
    var msg: String? = null
    var data: GrabSearchInfo = GrabSearchInfo()

    class GrabSearchInfo {
        var num: Int = 0
        var data: List<CustomerData.CustomerList> = ArrayList()
    }
}
