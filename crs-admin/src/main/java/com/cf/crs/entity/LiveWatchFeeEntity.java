package com.cf.crs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
 * 直播观看扣费
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("u_live_watch_fee")
@Builder
@ApiModel("直播观看扣费")
@NoArgsConstructor
@AllArgsConstructor
public class LiveWatchFeeEntity implements Serializable {


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
    @Excel(name = "消费金额")
    @ApiModelProperty(value = "消费金额")
    private BigDecimal totalAmount;

    /**
     * 收益金额
     */
    @Excel(name = "收益金额")
    @ApiModelProperty(value = "收益金额")
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
    @Excel(name = "扣费时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "扣费时间")
    private Long createTime;

    /**
     * 当前直播唯一标识
     */
    @Excel(name = "当前直播唯一标识")
    @ApiModelProperty(value = "当前直播唯一标识,方便后台统计当次观看时长")
    private String liveId;

    /**
     * 当前直播唯一标识
     */
    @Excel(name = "扣费类型")
    @ApiModelProperty(value = "扣费类型（0:分钟）")
    private Integer feeType;


}


