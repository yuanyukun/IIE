package com.common;

/**
 * Created by user on 2016/4/6.
 */
public class HttpUrlConstant {


    public static final String key = "?key=BA596D5703EE443B9DD2922EEDAE8088";
    //服务器地址
    public static final String baseUrlString = "http://www.paraboy.com";
//    public static final String baseUrlString = "http://192.168.1.5";
    //接口版本
    public static final String apiVersion3 = "/api/v3";
    //登录注册
    public static final String keyUrl = baseUrlString+apiVersion3 +"/key?";
    public static final String LogoutUrl = baseUrlString+apiVersion3 +"/member/logout/android"+key;
    public static final String LoginUrl = baseUrlString+apiVersion3 +"/member/login"+key;
    public static final String memberRegisterUrl = baseUrlString+apiVersion3 +"/member/register"+key;
    public static final String memberPasswordForgotUrl = baseUrlString+apiVersion3 +"/member/password/forgot"+key;
    //发布任务
    public static final String taskAddTaskUrl = baseUrlString+apiVersion3 +"/task/add"+key;
    //子任务订单零钱支付
    public static final String taskChangeToPaySubTaskUrl = baseUrlString+apiVersion3 +"/task/changeToPay/subTask"+key;   //任务地点多个的时候调用
    //主任务订单零钱支付
    public static final String taskChangeToPayMainTaskUrl = baseUrlString+apiVersion3 +"/task/changeToPay/mainTask"+key; //任务地点只有一个时候

    //地图
    public static final String mapAreaSubTaskScopeUrl = baseUrlString+apiVersion3 +"/map/area/subTask/scope"+key;//获取地理范围内所有任务

    /// 执行任务-上传图片（逐一上传）taskOrder/delivery/upload
    public static final String taskOrderDeliveryUploadUrl = baseUrlString+apiVersion3 +"/taskOrder/delivery/upload"+key;

    //上传完成
    public static final String taskOrderDeliveryUrl = baseUrlString+apiVersion3 +"/taskOrder/delivery"+key;
    //我的零钱
    public static final String taskMyChangeUrl = baseUrlString+apiVersion3 +"/task/myChange"+key;
    //我投的伞
    public static final String myIssuanceTaskList = baseUrlString+apiVersion3 +"/myIssuance/task/list"+key;
    //我的零钱明细
    public static final String taskMyChangeListUrl = baseUrlString+apiVersion3 +"/task/myChangeList"+key;
    //上传视频
    public static final String taskVideoUploadUrl = baseUrlString+apiVersion3 +"/taskOrder/delivery/video/upload"+key;
    //微信提现
    public static final String taskWithdrawalWeChatUrl = baseUrlString+apiVersion3 +"/task/withdrawal/wechat"+key;
    //子任务详情（确认界面）
    public static final String subTaskInfoUrl = baseUrlString+apiVersion3 +"/subTask/info"+key;
    //获取小图
    public static final String getSmallImageUrl = baseUrlString+apiVersion3+"/image/small";
    //获取大图
    public static final String getBigImageUrl = baseUrlString+apiVersion3+"/image/big"+key;
    //获取头像
    public static final String getHeadImageUrl = baseUrlString+apiVersion3+"/member/image"+key;
    //获取视频预览图
    public static final String getVideoPreviewImgUrl = baseUrlString+apiVersion3+"/video/preview/";
    public static final String getVideoUrl = baseUrlString+apiVersion3+"/video/";
    //订单确认
    public static final String taskOrderConfirmUrl = baseUrlString+apiVersion3 +"/taskOrder/confirm"+key;
    //微信登陆
    public static final String memberWeChatLoginUrl = baseUrlString+ apiVersion3  +"/member/weChat/android/login"+key;
    //微信任务同意下单子任务
    public static final String taskWeChatUnifiedOrderSubTaskUrl = baseUrlString + apiVersion3 +"/task/weChat/unifiedOrder/subTask"+key;
    //微信任务统一下单主任务
    public static final String taskWeChatUnifiedOrderMainTaskUrl = baseUrlString + apiVersion3+"/task/weChat/unifiedOrder/mainTask"+key;
    //支付宝统一下单
    public static final String taskAliUnifiedOrderMainTaskUrl = baseUrlString + apiVersion3 +"/task/alipay/unifiedOrder/mainTask"+key;
    public static final String taskAliUnifiedOrderSubTaskUrl = baseUrlString + apiVersion3 +"/task/alipay/unifiedOrder/subTask"+key;
    //消息轮询接口
    public static final String isHasNewMessage = baseUrlString+apiVersion3 +"/list/unread"+key;
    //更新channelId
    public static final String updateChannelId = baseUrlString+apiVersion3 +"/addChannel"+key;
    //聊聊首页
    public static final String chatMessageHome = baseUrlString+apiVersion3 +"/message/home"+key;

    //联系人相关
    public static final String messageDeleteContracts = baseUrlString +apiVersion3 +"/message/personal/delete"+key;//删除联系人
    public static final String messageAddBlackLists = baseUrlString+apiVersion3 +"/message/blacklist/add"+key;//加黑名单
    public static final String messageDeleteBlackLists = baseUrlString+apiVersion3 +"/message/blacklist/delete"+key;//删除黑名单
    public static final String messageMyBlackLists = baseUrlString+apiVersion3 +"/message/blacklist/list"+key;//我的黑名单列表
//    public static final String messageUserLists = baseUrlString+apiVersion3 +"/message/userList"+key;//获取聊聊对话用户列表
    public static final String messageDetailCurrent = baseUrlString+apiVersion3 +"/message/detail/current"+key;//详细消息列表（当前）
    public static final String messageDetailForPage = baseUrlString+apiVersion3 +"/message/detail/forPage"+key;//详细消息列表（翻页）
    public static final String messageSend = baseUrlString+apiVersion3 +"/member/message/add"+key;//详细消息列表（翻页）

    public static final String taskGetTotalFee = baseUrlString+apiVersion3 +"/task/getTotalFee"+key;//发布任务显示金额

    public static final String mapAddressPayStandardsUrl = baseUrlString+apiVersion3 +"/map/address/payStandards"+key;//
    //全部不采用
    public static final String taskOrderNotConfirmUrl = baseUrlString+apiVersion3 +"/taskOrder/notConfirm"+key;
    //支付信息(mainTask)
    public static final String taskPayInfoMainTask = baseUrlString+apiVersion3 +"/task/payInfo/mainTask"+key;
    //支付信息(SubTask)
    public static final String taskPayInfoSubTask = baseUrlString+apiVersion3 +"/task/payInfo/subTask"+key;
    //查看用户信息
    public static final String taskMemberUserInfo= baseUrlString+apiVersion3 +"/member/userInfo"+key;
//    //获取用户头像
//    public static final String getUserHeaderIcon= baseUrlString+apiVersion3 +"/member/image";
    //我的接收列表
    public static final String myReceiveTaskListUrl = baseUrlString + apiVersion3 +"/myReceive/task/list" + key;
    public static final String taskCloseUrl = baseUrlString+apiVersion3 +"/task/close";

    //使用条款url
    public static final String getClauseUrl = baseUrlString+apiVersion3 +"/clauseUrl"+key;
    //Iphone操作指引url
    public static final String getGuideUrl = baseUrlString+apiVersion3 +"/androidGuideUrl"+key;
    //分享appUrl
    public static final String getShareAppUrl = baseUrlString+apiVersion3 +"/shareAppUrl"+key;
    //分享状态告知服务器
    public static final String ShareAppUrl = baseUrlString+apiVersion3 +"/share/app"+key;
    //发布任务-上传视频
    public static final String  taskAddVideoUrl= baseUrlString+apiVersion3 +"/task/add/video"+key;
    //发布任务-上传图片
    public static final String taskAddPicUploadUrl  =     baseUrlString+apiVersion3 +"/task/add/pic/upload"+key;
    //发布任务
    public static final String taskBuildPhotUrl   =     baseUrlString+apiVersion3 +"/task/add/pic"+key;
    public static final String videoDownloadUrl   =     baseUrlString+apiVersion3 +"/video/download/";
    public static final String balloonVideoView   =     baseUrlString+apiVersion3 +"/balloon/video/view"+key;


    //微信相关
//    public static final String memberWeChatIsRegisterUrl = baseUrlString+apiVersion3 +"/member/weChat/isRegister";
//    public static final String memberWeChatCheckEmailUrl = baseUrlString+apiVersion3 +"/member/weChat/checkEmail";


//    public static final String memberWeChatRegisterUrl = baseUrlString+apiVersion3 +"/member/weixin/register";


    //个人中心
//    public static final String mapAreaSubTaskListUrl = baseUrlString+apiVersion3 +"/map/area/subTask/list";
    //根据经纬度获取地址
    public static final String mapAddressUrl = baseUrlString+apiVersion3 +"/map/address"+key;

//    public static final String termsOfUseUrl = baseUrlString+"/Interface/ClauseView.aspx";
    public static final String memberDetailUrl = baseUrlString+apiVersion3 +"/member/detail"+key;
    public static final String memberPhoneEditUrl = baseUrlString+apiVersion3 +"/member/phone/edit"+key;
    public static final String memberPasswordEditUrl = baseUrlString+apiVersion3 +"/member/password/edit"+key;
    public static final String memberEmailEditUrl = baseUrlString+apiVersion3 +"/member/email/edit"+key;
    public static final String memberNickNameEditUrl = baseUrlString+apiVersion3 +"/member/nickName/edit"+key;
    public static final String memberPortraitEditUrl = baseUrlString+apiVersion3 +"/member/portrait/edit"+key;
//    public static final String memberWeChatUnbundlingUrl = baseUrlString+apiVersion3 +"/member/weChat/unbundling";
    public static final String memberWeChatBindUrl = baseUrlString+apiVersion3 +"/member/weChat/bind"+key;
//     public static final String addDeviceTokenUrl = baseUrlString+apiVersion3 +"/addTokenOrChannel";

    //添加我的常用地址
    public static final String addMyAddressUrl = baseUrlString+apiVersion3 +"/addMyAddress"+key;
    //我的常用地址
    public static final String myAddressListUrl = baseUrlString+apiVersion3 +"/myAddressList"+key;
    //子任务所有订单
    public static final String subTaskOrderListUrl = baseUrlString+apiVersion3 +"/subTask/taskOrder/list"+key;

//    public static final String alertDetailUrl = baseUrlString+apiVersion3 +"/alert/detail";
//    public static final String sharePictureUrl = baseUrlString+apiVersion3 +"/share/picture";

    public static final String memberPhotoAllUrl = baseUrlString+apiVersion3 +"/member/photo/all"+key;
//    public static final String memberUserInfoUrl = baseUrlString+apiVersion3 +"/member/userInfo";

    public static final String memberViewUserListUrl = baseUrlString+apiVersion3 +"/member/viewUserList"+key;//谁看过我
//    public static final String videoInfoUrl = baseUrlString+apiVersion3 +"/video/timeLengthAndSize/update";

    //自动升级接口
    public static final String androidVersion = baseUrlString+apiVersion3 +"/android/version"+key;

    /*********************************************balloon气球相关************************************************/
    public static final String balloonPicUpload = baseUrlString+apiVersion3 +"/balloon/add/pic/upload"+key;
    public static final String balloonPicAdd = baseUrlString+apiVersion3 +"/balloon/add/pic"+key;
    public static final String balloonVideoAdd = baseUrlString+apiVersion3 +"/balloon/add/video"+key;
    public static final String balloonHomeFragment = baseUrlString+apiVersion3 +"/balloon/balloonList"+key;

    public static final String balloonFollowAdd = baseUrlString+apiVersion3 +"/follow/add"+key;
    public static final String balloonFollowCancel = baseUrlString+apiVersion3 +"/follow/cancel"+key;
    public static final String balloonExtroInfo = baseUrlString+apiVersion3 +"/balloon/extInfo"+key;
    public static final String balloonShare = baseUrlString+apiVersion3 +"/shareBalloon"+key;
    public static final String subTaskShare = baseUrlString+apiVersion3 +"/shareSubTask"+key;
    public static final String balloonShareUrl = baseUrlString+apiVersion3 +"/shareBalloonUrl"+key;
    public static final String subTaskShareUrl = baseUrlString+apiVersion3 +"/shareSubTaskUrl"+key;
    public static final String balloonGetTotalFee = baseUrlString+apiVersion3 +"/balloon/getTotalFee"+key;
    public static final String balloonRequest = baseUrlString+apiVersion3 +"/balloon/request"+key;
    public static final String balloonInfoDetails = baseUrlString+apiVersion3 +"/balloon/info"+key;
    public static final String balloonDiscovery = baseUrlString+apiVersion3 +"/balloon/discovery"+key;
    public static final String balloonRecommend = baseUrlString+apiVersion3 +"/balloon/recommendList"+key;
    public static final String balloonHotList = baseUrlString+apiVersion3 +"/balloon/hotBalloonList"+key;
    public static final String balloonFlowersPrices = baseUrlString+apiVersion3 +"/flowers/price/toBalloon"+key;
    public static final String balloonAddFlowers = baseUrlString+apiVersion3 +"/flowers/add/toBalloon"+key;//对气球送花
    public static final String balloonAddUsers = baseUrlString+apiVersion3 +"/flowers/add/toUser"+key;//对人送花
    public static final String balloonLikeAdd = baseUrlString+apiVersion3 +"/balloon/givelike/add"+key;//加赞
    public static final String balloonLikeCancle = baseUrlString+apiVersion3 +"/balloon/giveLike/cancel"+key;//取消赞
    public static final String balloonLikeInfo = baseUrlString+apiVersion3 +"/balloon/giveLike/info"+key;//赞详情
    public static final String balloonLikeMeList = baseUrlString+apiVersion3 +"/balloon/giveLikeMeList"+key;//谁赞过我列表
    public static final String balloonMyGiveLikeList = baseUrlString+apiVersion3 +"/balloon/myGiveLikeList"+key;//我赞过的列表



   /**********************************************************送花相关******************************************************/
    public static final String flowersPayInfo = baseUrlString+apiVersion3 +"/flowers/payInfo"+key;//对气球送花
    public static final String flowersRestMoneyToPay = baseUrlString+apiVersion3 +"/flowers/changeToPay"+key;//送花零钱支付接口
    public static final String flowersWeChatPay = baseUrlString+apiVersion3 +"/flowers/weChat/unifiedOrder"+key;//送花微信支付接口
    public static final String flowersAliPay = baseUrlString+apiVersion3 +"/flowers/alipay/unifiedOrder"+key;//送花支付宝支付接口
    public static final String flowersToMeList = baseUrlString+apiVersion3 +"/flowers/flowersToMeList"+key;//送花零钱支付接口
    public static final String flowersToMemberList = baseUrlString+apiVersion3 +"/flowers/flowersToMemberList"+key;//送花列表
    public static final String flowersBeFollowed = baseUrlString+apiVersion3 +"/follow/userBeFollowList"+key;//粉丝列表
    public static final String flowersMyFollowList = baseUrlString+apiVersion3 +"/follow/userFollowList"+key;//关注列表
    public static final String flowersMyBalloonList = baseUrlString+apiVersion3 +"/balloon/userBalloonList"+key;//气球列表
    public static final String flowersPriceToUser = baseUrlString+apiVersion3 +"/flowers/price/toUser"+key;//送花界面对人
    public static final String messageUserList = baseUrlString+apiVersion3 +"/message/userList"+key;//获取聊聊对话用户列表

    /*******************************************************************************************************************/
    public static final String memberIntroduceEdit = baseUrlString + apiVersion3 + "/member/introduction/edit" + key;
    public static final String memberSexEdit = baseUrlString + apiVersion3 + "/member/sex/edit" + key;
    public static final String memberHomeEdit = baseUrlString + apiVersion3 + "/member/hometownCity/edit" + key;
    public static final String memberWorkUnitEdit = baseUrlString + apiVersion3 + "/member/workUnit/edit" + key;
    public static final String memberSchoolEdit = baseUrlString + apiVersion3 + "/member/school/edit" + key;
    public static final String memberSiteCityEdit = baseUrlString + apiVersion3 + "/member/siteCity/edit" + key;
    public static final String memberProfessionList = baseUrlString + apiVersion3 + "/member/professionList" + key;
    public static final String memberProfessionEdit = baseUrlString + apiVersion3 + "/member/profession/edit" + key;
    public static final String userBalloonList = baseUrlString + apiVersion3 + "/balloon/userBalloonList" + key;
    //
    public static final String checkAdsInfo = baseUrlString + apiVersion3 + "/adsViewUrl" + key;
    public static final String isFollow = baseUrlString + apiVersion3 + "/follow/isFollow" + key;

}
