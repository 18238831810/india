package com.cf.crs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 注册充值返利记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("u_cashin_rebate")
public class CashinRebateEntity {


    private Long id;

    /**
     * 用户ID
     */
    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @Excel(name = "直接推荐人")
    @ApiModelProperty(value = "直接推荐人")
    private Long direct;

    @Excel(name = "间接推荐人")
    @ApiModelProperty(value = "间接推荐人")
    private Long indirect;

    @Excel(name = "直接返利金额")
    @ApiModelProperty(value = "直接返利金额")
    private BigDecimal cashinDirect;

    @Excel(name = "间接返利金额")
    @ApiModelProperty(value = "间接返利金额")
    private BigDecimal cashinIndirect;

    @Excel(name = "返利时间")
    @ApiModelProperty(value = "返利时间")
    private Long createTime;


}
