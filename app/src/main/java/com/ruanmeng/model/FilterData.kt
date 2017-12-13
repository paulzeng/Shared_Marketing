package com.ruanmeng.model

import java.util.ArrayList

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-03-29 10:08
 */

class FilterData {
    var msgcode: String? = null
    var msg: String? = null
    var data: FilterInfo = FilterInfo()

    class FilterInfo {
        var list_count: Int = 0
        var list_data: List<CustomerData.CustomerList> = ArrayList()
    }
}
