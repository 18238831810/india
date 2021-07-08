package com.cf.crs.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("u_order")
public class OrderEntity {
    @ApiParam(hidden = true)
    private int id;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiParam(hidden = true)
    private long ctime;
    /**
     * 撮合时间
     */
    @Excel(name = "撮合时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiParam(hidden = true)
    private long utime;
    /**
     * 直播房间号
     */
    @Excel(name = "直播房间号")
    private String roomId;
    /**
     * 下注当事人ID
     */
    @Excel(name = "下注当事人ID")
    @ApiParam(hidden = true)
    private long uid;
    /**
     * 下单总值
     */
    @Excel(name = "下单总量")
    @ApiParam(name = "payment", value = "下单总量")
    private double payment;

    /**
     * 买涨跌或平这三个方向
     * rise fall   equal
     */
    @Excel(name = "下单方向",dict = "p_k6")
    @ApiParam(name = "buyDirection", allowableValues = "rise,fall,equal", value = "下单方向")
    private String buyDirection;
    /**
     * 上期价格
     */
    @Excel(name = "上期价格")
    @ApiParam(hidden = true)
    private String earlyStagePrice;
    /**
     * 上期时间
     */
    @Excel(name = "上期时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiParam(hidden = true)
    private long earlyStageTime;
    /**
     * 下期价格
     */
    @Excel(name = "下期价格")
    @ApiParam(hidden = true)
    private String nextStagePrice;
    /**
     * 下期时间
     */
    @Excel(name = "下期时间",dict = "t_yyyy-MM-dd HH:mm:ss")
    @ApiParam(hidden = true)
    private long nextStageTime;

    /**
     * 杠杆倍数
     */
    @Excel(name = "杠杆倍数")
    @ApiParam(hidden = true)
    private double lever;

    /**
     * 盈利
     */
    @Excel(name = "盈利")
    @ApiParam(hidden = true)
    private double profit;

    /**
     * 行情周期
     */
    @Excel(name = "行情周期")
    @ApiParam(hidden = true)
    private String marketCycle;

    /**
     * 状态
     * 0:刚生成，1是处理 -1：为撤销订单
     */
    @Excel(name = "状态",dict = "p_k7")
    @ApiParam(hidden = true)
    private int status;

}
