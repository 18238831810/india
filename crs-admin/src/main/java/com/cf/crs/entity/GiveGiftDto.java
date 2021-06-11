package com.cf.crs.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 赠送礼物参数
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@ApiModel("赠送礼物")
public class GiveGiftDto implements Serializable {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long uid;

    /**
     * 主播id
     */
    @ApiModelProperty(value = "主播id")
    private Long coverConsumeUserId;

    /**
     * 礼物id
     */
    @ApiModelProperty(value = "礼物id")
    private Integer giftId;

    /**
     * 礼物数量
     */
    @ApiModelProperty(value = "礼物数量")
    private Integer giftNum;


}
