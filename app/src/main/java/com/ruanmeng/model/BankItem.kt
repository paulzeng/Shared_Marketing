package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-02-20 16:47
 */
class BankItem : Serializable {
    var id: String? = null
    var bank_name: String? = null

    var type_name: String? = null
    var house_type: String? = null
    var area: String? = null
    var price: String? = null
    var images: String? = null
    var sts: String? = null
    var hall_type: String? = null
    var images_more: ArrayList<String> = ArrayList()
    var details: String = ""

    var title: String? = null
    var content: String? = null
    var create_time: String? = null

    constructor()

    constructor(id: String, bank_name: String) {
        this.id = id
        this.bank_name = bank_name
    }

    constructor(title: String,
                content: String,
                create_time: String) {
        this.title = title
        this.content = content
        this.create_time = create_time
    }

    constructor(type_name: String,
                house_type: String,
                area: String,
                price: String,
                images: String,
                sts: String,
                hall_type: String,
                details: String,
                images_more: ArrayList<String>) {
        this.type_name = type_name
        this.house_type = house_type
        this.area = area
        this.price = price
        this.images = images
        this.sts = sts
        this.hall_type = hall_type
        this.details = details
        this.images_more = images_more
    }

}