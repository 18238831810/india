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
public class OrderCashinParam implements Serializable {


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID",hidden = true)
    private Long uid;

    /**
     * 支付类型 1:UPI（目前只能为1）
     */
    @ApiModelProperty(value = "支付类型 1:UPI（目前只能为1）",required = false,dataType = "1")
    private Integer paymentId = 1;


    /**
     * 订单金额 最多保留两位小数点
     */
    @ApiModelProperty(value = "订单金额 最多保留两位小数点",required = true)
    private Float amount;


    /**
     * 支付方UPI UPI格式
     */
    @ApiModelProperty(value = "支付方UPI UPI格式",required = false)
    private String payerAccount;

    /**
     * 商品信息 32字符以内
     */
    @ApiModelProperty(value = "商品信息 32字符以内",required = false)
    private String goodsInfo;



}
