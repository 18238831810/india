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
@TableName("u_order_cashout")
public class OrderCashoutEntity implements Serializable {

    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 支付类型 1:UPI代付，3：IFSC代付
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
     * IFSC编码 IFSC编码为印度银行的编码，每个银行不一样，格式为：CBIN0000000，payment_id为3时必传此参数*
     */
    private String payerIfsc;

    /**
     * 收款人手机号 与用户银行卡绑定的手机号(印度手机号格式：例如9015****66)，错误的手机号将无法代付完成(UPI方式代付，不要求手机号的有效性，但必须格式正确)
     */
    private String payerMobile;

    /**
     * 收款人名称 32字符以内
     */
    private String payerName;

    /**
     * 收款人账号 payment_id为1时UPI格式（xxx@xxx），为3时银行卡号格式（10到32位数字）
     */
    private String payerAccount;


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

    /**
     * 审批状态 0：未审批，1：已审批
     */
    private Integer approveStatus = 0;

    /**
     * 备注
     */
    private String remark;


}
