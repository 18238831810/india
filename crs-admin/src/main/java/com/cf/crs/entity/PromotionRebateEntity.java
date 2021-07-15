package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 抽成设置
 * @author frank
 * @date 20210-07-15
 *
 **/
@Data
@TableName("u_promotion_rebate")
@Builder
@ApiModel("推广返利规则设置")
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRebateEntity implements Serializable {


    @TableId
    private Long id;

    /**
     * 注册直接返利金额
     */
    @ApiModelProperty(name = "注册直接返利金额")
    private BigDecimal cashinDirect;

    /**
     * 注册间接返利金额
     */
    @ApiModelProperty(name = "注册间接返利金额")
    private BigDecimal cashinIndirect;

    /**
     * 下单佣金百分比
     */
    @ApiModelProperty(name = "下单佣金百分比")
    private Integer orderCommission;

    /**
     * 下单间接返利百分比
     */
    @ApiModelProperty(name = "下单直接返利百分比")
    private Integer orderDirect;

    /**
     * 下单间接返利百分比
     */
    @ApiModelProperty(name = "下单间接返利百分比")
    private Integer orderIndirect;


    /**
     * 更新时间
     */
    @ApiModelProperty(name = "更新时间")
    private Long updateTime;



}
