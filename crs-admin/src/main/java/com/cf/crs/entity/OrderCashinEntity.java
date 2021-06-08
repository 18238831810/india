package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("u_order_cashin")
public class OrderCashinEntity implements Serializable {

    @TableId
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    /**
     * 支付类型 1:UPI（目前只能为1）
     */
    @ApiModelProperty(value = "支付类型 1:UPI")
    private Integer paymentId = 1;

    /**
     * 订单号 32字符以内，不能重复
     */
    @ApiModelProperty(value = "订单号")
    private String orderSn;

    /**
     * 支付平台生成的订单号
     */
    @ApiModelProperty(value = "支付平台生成的订单号")
    private String ptOrderSn;

    /**
     * 订单金额 最多保留两位小数点
     */
    @ApiModelProperty(value = "订单金额")
    private Float amount;

    /**
     * 实际存款金额 最多保留两位小数点
     */
    @ApiModelProperty(value = "实际存款金额")
    private Float realAmount;

    /**
     * 支付方UPI UPI格式
     */
    @ApiModelProperty(value = "支付方UPI")
    private String payerAccount;

    /**
     * 商品信息 32字符以内
     */
    @ApiModelProperty(value = "商品信息")
    private String goodsInfo;

    /**
     * 客户端ip
     */
    @ApiModelProperty(value = "客户端ip")
    private String ip;

    /**
     * 订单时间
     */
    @ApiModelProperty(value = "订单时间")
    private Long orderTime;

    /**
     * 实际到账时间
     */
    @ApiModelProperty(value = "实际到账时间")
    private Long dealTime;

    /**
     * 订单状态 0:未完成 1:失败 2:成功
     */
    @ApiModelProperty(value = "订单状态 0:未完成 1:失败 2:成功")
    private Integer status;



}
