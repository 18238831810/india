package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 资金明细
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("u_financial_detail")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDetailsEntity implements Serializable {

    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 订单类型（1:存款，2:提现 3:下单）
     */
    private Integer type;

    /**
     * 订单号 (存款款订单号，下单单号)
     */
    private String orderSn;

    /**
     * 订单金额（金额的变化） 最多保留两位小数点
     */
    private Float amount;

    /**
     * 订单时间
     */
    private Long orderTime;

    /**
     * 操作后用户余额
     */
    private Float balance;

    /**
     * 备注
     */
    private String remark;


}
