package com.cf.crs.entity;

import com.cf.crs.common.entity.QueryPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户余额表
 */
@Data
@ApiModel(value = "用户余额")
public class AccountBalanceDto extends QueryPage implements Serializable {


    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long uid;

    @ApiModelProperty(value = "用户类型 0:普通用户 1:主播")
    private Integer type;

}
