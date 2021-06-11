package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户余额表
 */
@Data
@ApiModel(value = "用户余额")
@TableName("u_account_balance")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceEntity {




    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    /**
     * 用户总金额
     */
    @ApiModelProperty(value = "用户总金额")
    private BigDecimal amount;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;

}
