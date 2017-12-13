/**
 * created by 小卷毛, 2016/11/22
 * Copyright (c) 2016, 416143467@qq.com All Rights Reserved.
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
 * #               佛祖保佑         永无BUG            #
 * #                                                   #
 */
package com.ruanmeng.share;

import com.ruanmeng.shared_marketing.BuildConfig;

/**
 * 项目名称：Shared_Marketing
 * 创建人：小卷毛
 * 创建时间：20176-02-16 18:45
 */
public class HttpIP {

    public static String IP = BuildConfig.API_HOST;
    private static String URL = IP + "/app/Public/Zhd/?service=";

    // 用户
    public static String register = URL + "User.register";                       //用户注册
    public static String login = URL + "User.login";                             //用户登录
    public static String forgetPass = URL + "User.forgetPass";                   //忘记密码
    public static String userInfo = URL + "User.userInfo";                       //用户中心首页
    public static String modifyLogo = URL + "User.modifyLogo";                   //修改头像
    public static String modifyNickname = URL + "User.modifyNickname";           //修改昵称
    public static String modifyGender = URL + "User.modifyGender";               //修改性别
    public static String modifyPass = URL + "User.modifyPass";                   //修改登录密码
    public static String modifyPaypass = URL + "User.modifyPaypass";             //修改支付密码
    public static String auth = URL + "User.auth";                               //实名认证
    public static String exchange = URL + "User.exchange";                       //积分兑换
    public static String scoreList = URL + "User.scoreList";                     //积分获取记录
    public static String exchangeList = URL + "User.exchangeList";               //积分兑换记录
    public static String commissionDetail = URL + "User.commissionDetail";       //查看累计佣金
    public static String teamRecDetail = URL + "User.teamRecDetail";             //团队推荐详情
    public static String withdraw = URL + "User.withdraw";                       //提现
    public static String withdrawList = URL + "User.withdrawList";               //提现记录
    public static String bookCar = URL + "User.bookCar";                         //一键约车
    public static String feedback = URL + "User.feedback";                       //投诉建议反馈
    public static String appVersion = URL + "User.appVersion";                   //版本更新
    public static String vipCommissionList = URL + "User.vipCommissionList";     //VIP佣金列表
    public static String commissionList = URL + "User.commissionList";           //未/已结佣金
    public static String vipCommissionDetail = URL + "User.vipCommissionDetail"; //VIP单个佣金详情
    public static String feedbackList = URL + "User.feedbackList";               //投诉建议反馈列表
    public static String feedbackCount = URL + "User.feedbackCount";             //投诉建议反馈数量
    public static String commissionWHList = URL + "User.commissionWHList";       //提现佣金列表
    public static String withdrawV2 = URL + "User.withdrawV2";                   //提现V2

    // 首页
    public static String noticeList = URL + "Home.noticeList";                      //公告列表
    public static String customerList = URL + "Home.customerList";                  //客户列表
    public static String noticeDetail = URL + "Home.noticeDetail";                  //公告详情
    public static String recCustomer = URL + "Home.recCustomer";                    //推荐客户
    public static String searchCustomer = URL + "Home.searchCustomer";              //搜索客户
    public static String slider = URL + "Home.slider";                              //轮播图
    public static String searchCustomerByTime = URL + "Home.searchCustomerByTime";  //搜索客户_带筛选

    // 团队
    // public static String teamRecDetail = URL + "User.teamRecDetail"; //团队列表
    public static String search = URL + "Team.search";   //团队搜索
    public static String devTeam = URL + "Team.devTeam"; //发展下线

    // 楼盘
    public static String buildingList = URL + "Proj.buildingList";     //楼盘列表
    public static String buildingDetail = URL + "Proj.buildingDetail"; //楼盘详情

    // 公共
    public static String getProvinceList = URL + "Common.getProvinceList";       //省份列表
    public static String getCityList = URL + "Common.getCityList";               //城市列表
    public static String getDistrictList = URL + "Common.getDistrictList";       //区县列表
    public static String sysParam = URL + "Common.sysParam";                     //系统参数
    public static String contentShow = URL + "Common.contentShow";               //内容展示
    public static String verifyCode = URL + "Common.verifyCode";                 //验证码
    public static String getSystemTimestamp = URL + "System.getSystemTimestamp"; //获取系统时间戳
    public static String getLocationId = URL + "Common.getLocationId";           //根据名称获取地区id
    public static String getOpenCityList = URL + "Common.getOpenCityList";       //开通城市
    public static String recCusexplain = URL + "Home.recCusexplain";             //推荐用户说明

    // 案场经理
    public static String modifyConsultant = URL + "Anchang.modifyConsultant";             //修改置业顾问
    public static String consultantList = URL + "Anchang.consultantList";                 //置业顾问列表
    public static String consultantDetail = URL + "Anchang.consultantDetail";             //置业顾问详情
    public static String consultantLists = URL + "Anchang.consultantLists";               //置业顾问列表（无分页）
    public static String batchModifyConsultant = URL + "Anchang.batchModifyConsultant";   //批量修改置业顾问

    // 渠道经理
    public static String companyList = URL + "Channel.companyList";           //合作单位列表
    public static String partnerList = URL + "Channel.partnerList";           //合作单位合伙人列表
    public static String customerDetail = URL + "Home.customerDetail";        //推荐客户详情
    public static String recList = URL + "Channel.recList";                   //推荐客户列表
    public static String commissionrecord = URL + "Channel.commissionrecord"; //佣金记录

    // 驻场
    public static String check = URL + "Zhuchang.check";                   //审核客户
    public static String call = URL + "Zhuchang.call";                     //去电
    public static String visit = URL + "Zhuchang.visit";                   //到访
    public static String subscribe = URL + "Zhuchang.subscribe";           //认购
    public static String signContract = URL + "Zhuchang.signContract";     //签约
    public static String receivePayment = URL + "Zhuchang.receivePayment"; //回款（结佣）

    // 置业顾问
    public static String grabCustomer = URL + "Consultant.grabCustomer";         //抢单
    public static String Customerlist_new = URL + "Consultant.Customerlist_new"; //抢单列表

    // 二期接口
    public static String myScoreV2 = URL + "User.myScoreV2";         //我的积分
    public static String myScoreListV2 = URL + "user.myScoreListV2"; //积分记录
    public static String prizeList = URL + "user.prizeList";         //中奖记录

    public static String bookCarList = URL + "driver.bookCarList"; //抢单列表
    public static String grabOrder = URL + "driver.grabOrder"; //抢单
    public static String orderList = URL + "driver.orderList";     //订单列表
    public static String finishOrder = URL + "driver.finishOrder"; //结束订单

    public static String departmentList = URL + "qdzy.departmentList"; //分组（部门）列表
    public static String customerList2 = URL + "qdzy.customerList";    //渠道专员客户列表

    public static String companyList2 = URL + "fxzy.companyList";   //合作企业列表
    public static String teamList = URL + "fxzy.teamList";          //团队推荐列表
    public static String customerList3 = URL + "fxzy.customerList"; //客户列表


    /**
     * 加密规则：
     *   1.在调用所有的接口的时候，都需要传入参数time和token两个参数。
     *   2.time为当前的时间戳，token是根据time和一定的算法计算出来的一个令牌。
     *   3.服务端会根据time生成token，和客户端传递过来的token进行比较，如果一致，则运行调用接口，否则禁止访问接口。
     *   4.token计算方法为：md5(salt + microtime + hash);
     */

    public static String staticimage = "http://api.map.baidu.com/staticimage/v2?markerStyles=l,A,0xff0000&zoom=17&ak=DvQuuNLP55unm8snCWsTkVFWaSSMn9hT&mcode=FC:E5:F9:DE:65:1A:D0:23:1D:0D:60:FE:CF:C6:AF:9E:8C:E0:EA:38;com.ruanmeng.shared_marketing&copyright=1"; //百度静态图

}
