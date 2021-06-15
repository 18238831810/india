package com.cf.crs.common.constant;

public class MsgError {

    //权限不足
    public static  final  String  AUTH_FAIL= "msg_100";

    //请输入提现金额
    public static  final  String  CASHOUT_AMOUNT_EMPTY= "msg_1000";
    //请输入收款人账号
    public static  final  String  CASHOUT_ACCOUNT_EMPTY= "msg_1001";
    //请输入收款人手机号
    public static  final  String  CASHOUT_PHONE_EMPTY= "msg_1002";
    //请输入收款人名称
    public static  final  String  CASHOUT_NAME_EMPTY= "msg_1003";
    //请输入IFSC编码
    public static  final  String  CASHOUT_IFSC_EMPTY= "msg_1004";

    //此提案不存在或者已审批
    public static  final  String  CASHOUT_APPROVE_EXIST= "msg_1005";

    //用户提款金额不足,审批失败
    public static  final  String  CASHOUT_APPROVE_AMOUNT_FAIL= "msg_1006";

    //提现失败
    public static  final  String  CASHOUT_APPROVE_FAIL= "msg_1007";

    //请求参数异常
    public static  final  String  PARAM_ERROR= "msg_1008";
    //请勿过于频繁操作
    public static  final  String  REQUEST_FREQUENTLY= "msg_1009";
    //余额不足!充值
    public static  final  String  BALANCE_NOT_ENOUGH= "msg_1010";
    //发送礼物失败
    public static  final  String  SEND_FAIL= "msg_1011";


}
