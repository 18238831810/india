package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 支付类型 1:UPI（目前只能为1）
     */
    private Integer paymentId = 1;

    /**
     * 订单号 32字符以内，不能重复
     */
    private String orderSn;

    /**
     * 支付平台生成的订单号
     */
    private String ptOrderSn;

    /**
     * 订单金额 最多保留两位小数点
     */
    private Float amount;

    /**
     * 实际存款金额 最多保留两位小数点
     */
    private Float realAmount;

    /**
     * 支付方UPI UPI格式
     */
    private String payerAccount;

    /**
     * 商品信息 32字符以内
     */
    private String goodsInfo;

    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 订单时间
     */
    private Long orderTime;

    /**
     * 实际到账时间
     */
    private Long dealTime;

    /**
     * 订单状态 0:未完成 1:失败 2:成功
     */
    private Integer status;



}
