package com.cf.crs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 直播观看扣费
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("u_live_watch_fee_result")
@Builder
@ApiModel("直播观看扣费")
@NoArgsConstructor
@AllArgsConstructor
public class LiveWatchFeeResultEntity implements Serializable {


    @TableId
    private Long id;

    /**
     * 消费用户id
     */
    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private Long uid;

    /**
     * 被消费人id
     */
    @Excel(name = "主播账号")
    @ApiModelProperty(value = "主播账号")
    private Long coverId;

    /**
     * 消费金额
     */
    @Excel(name = "消费总金额")
    @ApiModelProperty(value = "消费总金额")
    private BigDecimal totalAmount;

    /**
     * 收益金额
     */
    @Excel(name = "收益总金额")
    @ApiModelProperty(value = "收益总金额")
    private BigDecimal coverAmount;

    /**
     * 直播类型（0:直播，1:录播）
     */
    @Excel(name = "直播类型（0:直播，1:录播）",dict = "p_k5")
    @ApiModelProperty(value = "直播类型（0:直播，1:录播）")
    private Integer type;

    /**
     * 扣费时间
     */
    @Excel(name = "开始时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Long createTime;

    /**
     * 扣费时间
     */
    @Excel(name = "汇总时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "汇总时间")
    private Long updateTime;

    /**
     * 当前直播唯一标识
     */
    @Excel(name = "当单直播观看唯一标识")
    @ApiModelProperty(value = "当前直播唯一标识,方便后台统计当次观看时长")
    private String liveId;

    /**
     * 当前直播唯一标识
     */
    @Excel(name = "扣费类型")
    @ApiModelProperty(value = "扣费类型（0:分钟）")
    private Integer feeType;

    @Excel(name = "观看时长")
    @ApiModelProperty(value = "观看时长（分钟）")
    private Integer duration;


}


