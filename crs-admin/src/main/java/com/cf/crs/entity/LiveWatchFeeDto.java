package com.cf.crs.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 直播观看扣费
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@ApiModel("直播观看扣费")
public class LiveWatchFeeDto implements Serializable {

    /**
     * 消费用户id
     */
    @ApiModelProperty(value = "用户账号",hidden = true)
    private Long uid;

    /**
     * 被消费人id
     */
    @ApiModelProperty(value = "主播账号",required = true)
    private Long coverId;


    /**
     * 直播类型（0:直播，1:录播）
     */
    @ApiModelProperty(value = "直播类型（0:直播，1:录播）")
    private Integer type = 0;


    /**
     * 当前直播唯一标识
     */
    @ApiModelProperty(value = "当单直播观看唯一标识,方便后台统计当次观看时长",required = true)
    private String liveId;


}
