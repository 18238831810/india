package com.cf.crs.entity;

import com.cf.crs.common.entity.QueryPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 存款表
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@ApiModel(value = "提现下单参数")
public class OrderCashoutDto extends QueryPage implements Serializable {


    @ApiModelProperty(value = "用户ID",required = true)
    private Long uid;

    @ApiModelProperty(value = "开始时间",required = false)
    private Long startTime;

    @ApiModelProperty(value = "结束时间",required = false)
    private Long endTime;




}
