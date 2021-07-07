package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.cf.crs.common.entity.QueryPage;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 抽成设置
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@ApiModel("用户消费记录")
public class ConsumeDto extends QueryPage implements Serializable {


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
     * 消费类型（1:赠送礼物，即打赏主播）
     */
    private Integer type;




}
