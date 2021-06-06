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
@TableName("u_order_commission")
public class OrderCommissionEntity {
    private int id;
    /**
     * 返利总量
     */
    private double profit;
    /**
     * 直播间ID
     */
    private String roomId;
    /**
     * 返利时间
     */
    private long ctime;
    /**
     * 是否已经提案，比如已提现，或已经可以提现.
     * 0:刚生成  1：提案  2：提现
     */
    private int status;
}
