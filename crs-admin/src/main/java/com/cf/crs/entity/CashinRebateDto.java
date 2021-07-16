package com.cf.crs.entity;

import com.cf.crs.common.entity.QueryPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 注册充值返利记录
 */
@Data
public class CashinRebateDto extends QueryPage implements Serializable {


    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "直接推荐人")
    private Long direct;

    @ApiModelProperty(value = "间接推荐人")
    private Long indirect;

    @ApiModelProperty(value = "直接返利金额")
    private BigDecimal cashinDirect;

    @ApiModelProperty(value = "间接返利金额")
    private BigDecimal cashinIndirect;

    @ApiModelProperty(value = "返利时间")
    private Long createTime;

    @ApiModelProperty(value = "返利类型（0:注册充值返利,1:交易返利）")
    private Integer type = 0;


}
