package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户余额表
 */
@Data
@TableName("u_account_balance")
public class AccountBalanceEntity {


    private Long id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 用户总金额
     */
    private Float amount;

    /**
     * 更新时间
     */
    private Long updateTime;

}
