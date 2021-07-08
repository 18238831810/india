package com.cf.crs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    /**
     * 用户手机号
     */
    @Excel(name = "用户手机号")
    @ApiModelProperty(value = "用户手机号")
    private String phone;

    /**
     * 用户总金额
     */
    @Excel(name = "用户总金额")
    @ApiModelProperty(value = "用户总金额")
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 更新时间
     */
    @Excel(name = "更新时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Long updateTime;

    //@Excel(name = "打赏收益")
    @ApiModelProperty(value = "打赏收益")
    private BigDecimal consumeCount = BigDecimal.ZERO;

    //@Excel(name = "交易流水分层")
    @ApiModelProperty(value = "交易流水分层")
    private BigDecimal orderCount = BigDecimal.ZERO;

}
