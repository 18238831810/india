package com.cf.crs.properties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author frank
 * @description 声网token参数
 * @date 2019/8/13 16:06
 */
@Data
@ApiModel(value = "声网获取token请求参数")
public class AgoraParam implements Serializable {

    /**
     * 频道名
     */
    @ApiModelProperty(name = "channelName",value = "频道名",required = true)
    private String channelName;

    /**
     * 用户id
     */
    @ApiModelProperty(name = "uid",value = "用户id",hidden = true)
    private Long uid;


}
