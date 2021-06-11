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

/**
 * 抽成设置
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("u_consume")
@Builder
@ApiModel("用户消费记录")
@NoArgsConstructor
@AllArgsConstructor
public class ConsumeEntity implements Serializable {


    @TableId
    private Long id;

    /**
     * 消费用户id
     */
    private Long uid;

    /**
     * 被消费人id
     */
    private Long coverId;

    /**
     * 消费金额
     */
    private BigDecimal totalAmount;

    /**
     * 收益金额
     */
    private BigDecimal coverAmount;

    /**
     * 消费类型（1:赠送礼物，即打赏主播）
     */
    private Integer type;

    private Long createTime;

    /**
     * 备注
     */
    private String remark;


}
