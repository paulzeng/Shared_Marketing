package com.ruanmeng.model

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-05-23 10:28
 */

class FeedbackData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : [{"id":"9","user_id":"171","content":"把赢宝马的图片和方案列出来","reply_content":"感谢您对我们的支持!","reply_time":"2017-04-06 08:32","create_time":"2017-04-06 08:32"},{"id":"1","user_id":"171","content":"身份证号无法输入 X","reply_content":"","reply_time":"","create_time":"2017-03-21 09:28"}]
     */

    var msgcode: Int = 0
    var msg: String? = null
    var data: List<FeedbackInfo> = ArrayList()

    class FeedbackInfo {
        /**
         * id : 9
         * user_id : 171
         * content : 把赢宝马的图片和方案列出来
         * reply_content : 感谢您对我们的支持!
         * reply_time : 2017-04-06 08:32
         * create_time : 2017-04-06 08:32
         */

        var id: String? = null
        var user_id: String? = null
        var content: String? = null
        var reply_content: String = ""
        var reply_time: String? = null
        var create_time: String? = null

        constructor(content: String, reply_content: String) {
            this.content = content
            this.reply_content = reply_content
        }
    }
}
