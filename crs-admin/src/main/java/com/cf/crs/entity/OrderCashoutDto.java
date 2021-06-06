package com.cf.crs.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 存款表
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@ApiModel(value = "存款下单参数")
public class OrderCashoutDto implements Serializable {


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID",required = true)
    private Long uid;

    /**
     * 支付类型 1:UPI（目前只能为1）
     */
    @ApiModelProperty(value = "1:UPI代付，3：IFSC代付",required = true)
    private Integer paymentId;


    /**
     * 订单金额 最多保留两位小数点
     */
    @ApiModelProperty(value = "订单金额 最多保留两位小数点",required = true)
    private Float amount;

    /**
     * IFSC编码 IFSC编码为印度银行的编码，每个银行不一样，格式为：CBIN0000000，payment_id为3时必传此参数*
     */
    @ApiModelProperty(value = "IFSC编码为印度银行的编码，每个银行不一样，格式为：CBIN0000000，payment_id为3时必传此参数*",required = false)
    private String payerIfsc;


    /**
     * 支付方UPI UPI格式
     */
    @ApiModelProperty(value = "收款人账号,payment_id为1时UPI格式（xxx@xxx），为3时银行卡号格式（10到32位数字）",required = true)
    private String payerAccount;

    /**
     * 收款人手机号 与用户银行卡绑定的手机号(印度手机号格式：例如9015****66)，错误的手机号将无法代付完成(UPI方式代付，不要求手机号的有效性，但必须格式正确)
     */
    @ApiModelProperty(value = "收款人手机号 与用户银行卡绑定的手机号(印度手机号格式：例如9015****66)，错误的手机号将无法代付完成(UPI方式代付，不要求手机号的有效性，但必须格式正确)",required = true)
    private String payerMobile;

    /**
     * 收款人名称 32字符以内
     */
    @ApiModelProperty(value = "收款人名称 32字符以内",required = true)
    private String payerName;



}
