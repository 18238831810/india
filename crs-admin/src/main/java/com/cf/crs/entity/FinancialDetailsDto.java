package com.cf.crs.entity;

import com.cf.crs.common.entity.QueryPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("资金明细查询")
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDetailsDto extends QueryPage implements Serializable {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户id",required = true)
    private Long uid;

    /**
     * 订单类型（1:存款，2:提现 3:下单）
     */
    @ApiModelProperty(value = "订单类型（1:存款，2:提现 3:下单）",required = false)
    private Integer type;

    /**
     * 订单时间
     */
    @ApiModelProperty(value = "开始时间",required = false)
    private Long startTime;

    /**
     * 订单时间
     */
    @ApiModelProperty(value = "结束时间",required = false)
    private Long endTime;




}
