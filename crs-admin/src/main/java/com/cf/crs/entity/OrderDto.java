package com.cf.crs.entity;

import com.cf.crs.common.entity.QueryPage;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto extends QueryPage implements Serializable {
    /**
     * 创建时间
     */
    @ApiParam(hidden = true)
    private long ctime;
    /**
     * 撮合时间
     */
    @ApiParam(hidden = true)
    private long utime;
    /**
     * 直播房间号
     */
    private String roomId;
    /**
     * 下注当事人ID
     */
    @ApiParam(hidden = true)
    private Long uid;
    /**
     * 下单总值
     */
    @ApiParam(name = "payment", value = "下单总量")
    private double payment;

    /**
     * 买涨跌或平这三个方向
     * rise fall   equal
     */
    @ApiParam(name = "buyDirection", allowableValues = "rise,fall,equal", value = "下单方向")
    private String buyDirection;
    /**
     * 上期价格
     */
    @ApiParam(hidden = true)
    private String earlyStagePrice;
    /**
     * 上期时间
     */
    @ApiParam(hidden = true)
    private long earlyStageTime;
    /**
     * 下期价格
     */
    @ApiParam(hidden = true)
    private String nextStagePrice;
    /**
     * 下期时间
     */
    @ApiParam(hidden = true)
    private long nextStageTime;

    /**
     * 杠杆倍数
     */
    @ApiParam(hidden = true)
    private double lever;

    /**
     * 盈利
     */
    @ApiParam(hidden = true)
    private double profit;

    /**
     * 行情周期
     */
    @ApiParam(hidden = true)
    private String marketCycle;

    /**
     * 状态
     * 0:刚生成，1是处理 -1：为撤销订单
     */
    @ApiParam(hidden = true)
    private int status;

    @ApiModelProperty(value = "开始时间",required = false)
    private Long startTime;

    @ApiModelProperty(value = "结束时间",required = false)
    private Long endTime;



}
