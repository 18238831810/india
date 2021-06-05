package com.cf.crs.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 代收款参数
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
public class OrderParam implements Serializable {

    /**
     * 商户id
     */
    private Integer merch_id;

    /**
     * 支付类型 1:UPI（目前只能为1）
     */
    private Integer payment_id = 1;

    /**
     * 订单号 32字符以内，不能重复
     */
    private String order_sn;

    /**
     * 订单金额 最多保留两位小数点
     */
    private Float amount;

    /**
     * 支付方UPI UPI格式
     */
    private String payer_account;

    /**
     * 商品信息 32字符以内
     */
    private String goods_info;

    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 异步回调地址 用户支付成功后回调地址。请注意：回调地址中不可以带参数！
     */
    private String notify_url;

    /**
     * 签名
     * 1、按字典正序排序传入的参数（sign除外）
     * 2、组装待签名字符串：a=v1&b=v2&c=v3...&key=商户的api_token
     * 3、sign的值 = md5(第2步中得到的待签名的字符串)
     * 4、将得到的sign值加入传参。
     */
    private String sign;

}
