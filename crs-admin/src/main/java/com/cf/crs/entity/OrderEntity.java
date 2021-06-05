package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private int id;
    /**
     * 创建时间
     */
    private long ctime;
    /**
     * 撮合时间
     */
    private long utime;
    /**
     * 直播房间号
     */
    private String roomId;
    /**
     * 下注当事人ID
     */
    private long uid;
    /**
     * 下单总值
     */
    private double payment;

    /**
     * 买涨跌或平这三个方向
     * rise fall   equal
     */
    private String buyDirection;
    /**
     * 上期价格
     */
    private String earlyStagePrice;
    /**
     * 上期时间
     */
    private long earlyStageTime;
    /**
     * 下期价格
     */
    private String nextStagePrice;
    /**
     * 下期时间
     */
    private long nextStageTime;

    /**
     * 杠杆倍数
     */
    private double lever;

    /**
     * 盈利
     */
    private double profit;

    /**
     * 行情周期
     */
    private String marketCycle;

    @TableField(exist = false)
    private String token;

}
