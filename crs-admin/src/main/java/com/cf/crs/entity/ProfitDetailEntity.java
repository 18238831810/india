package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 抽成设置
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("t_profit_detail")
@Builder
@ApiModel("收益明细表")
@NoArgsConstructor
@AllArgsConstructor
public class ProfitDetailEntity implements Serializable {

    @TableId
    private Integer tProfitId;

    /**
     * 收益类型
     */
    private Integer tProfitType;

    /**
     * 收益金额
     */
    private BigDecimal tProfitGold;

    private Timestamp tCreateTime;


}
