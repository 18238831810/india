package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("u_order_lever")
public class OrderLeverEntity {

    private String name;
    /**
     * 类型，平涨跌 0 为买卖方向  1为下单设置
     */
    private int type;
    /**
     * 倍数
     */
    private double lever;
}
