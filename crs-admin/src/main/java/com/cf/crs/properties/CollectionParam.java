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
public class CollectionParam implements Serializable {

    /**
     * 商户id
     */
    private Integer merch_id;

    /**
     * 支付类型 1:UPI代付，3：IFSC代付
     */
    private Integer payment_id = 1;

    /**
     * 订单号 32字符以内，不能重复
     */
    private String order_sn;

    /**
     * 订单金额 最多保留两位小数点,170.00传170
     */
    private Float amount;


    /**
     * IFSC编码 IFSC编码为印度银行的编码，每个银行不一样，格式为：CBIN0000000，payment_id为3时必传此参数*
     */
    private String payer_ifsc;


    /**
     * 收款人账号 payment_id为1时UPI格式（xxx@xxx），为3时银行卡号格式（10到32位数字）
     */
    private String payer_account;

    /**
     * 收款人手机号	与用户银行卡绑定的手机号(印度手机号格式：例如9015****66)，错误的手机号将无法代付完成(UPI方式代付，不要求手机号的有效性，但必须格式正确)
     */
    private String payer_mobile;

    /**
     * 收款人名称 32字符以内
     */
    private String payer_name;

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
