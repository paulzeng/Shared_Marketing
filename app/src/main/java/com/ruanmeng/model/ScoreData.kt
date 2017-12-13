package com.ruanmeng.model

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：2017-05-31 9:15
 */

class ScoreData {
    /**
     * msgcode : 1
     * msg : 操作成功！
     * data : {"logo":"https://hkt.kaiyuanyihao.com//data/upload/app/2017523/1495517489_711636.jpg","score":"888","score_intro":"积分说明","game":[{"id":"1","name":"大转盘开始了","start_time":"2017-02-27 11:42","end_time":"2017-02-27 11:42","cover_img":"/data/upload/app/201748/1491632644_771072.jpg","l_status":"1"},{"id":"1","name":"砸金蛋开始了","start_time":"2017-02-27 11:42","end_time":"2017-02-27 11:42","cover_img":"/data/upload/app/201748/1491632644_771072.jpg","l_status":"1"}]}
     */

    var msgcode: String? = null
    var msg: String? = null
    var data: ScoreInfo = ScoreInfo()

    class ScoreInfo {
        /**
         * logo : https://hkt.kaiyuanyihao.com//data/upload/app/2017523/1495517489_711636.jpg
         * score : 888
         * score_intro : 积分说明
         * game : [{"id":"1","name":"大转盘开始了","start_time":"2017-02-27 11:42","end_time":"2017-02-27 11:42","cover_img":"/data/upload/app/201748/1491632644_771072.jpg","l_status":"1"},{"id":"1","name":"砸金蛋开始了","start_time":"2017-02-27 11:42","end_time":"2017-02-27 11:42","cover_img":"/data/upload/app/201748/1491632644_771072.jpg","l_status":"1"}]
         */

        var logo: String? = null
        var score: String? = null
        var score_intro: String? = null
        var game: List<ScoreList> = ArrayList()
    }

    class ScoreList {
        /**
         * id : 1
         * name : 大转盘开始了
         * start_time : 2017-02-27 11:42
         * end_time : 2017-02-27 11:42
         * cover_img : /data/upload/app/201748/1491632644_771072.jpg
         * l_status : 1
         */

        var id: String? = null
        var name: String? = null
        var start_time: String? = null
        var end_time: String? = null
        var cover_img: String? = null
        var l_status: String? = null
        var url: String? = null
    }
}
