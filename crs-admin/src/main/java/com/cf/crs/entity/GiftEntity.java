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
 * 资金明细
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("t_gift")
@Builder
@ApiModel("赠送礼物列表")
@NoArgsConstructor
@AllArgsConstructor
public class GiftEntity implements Serializable {
    /*
     `t_gift_id` int(11) NOT NULL AUTO_INCREMENT,
  `t_gift_name` varchar(100) DEFAULT NULL,
  `t_gift_gif_url` varchar(100) DEFAULT NULL,
  `t_gift_still_url` varchar(150) DEFAULT NULL,
  `t_gift_gold` decimal(15,2) DEFAULT NULL,
  `t_is_enable` int(11) DEFAULT NULL COMMENT '0.启用\r\n1.停用',
            `t_create_time` datetime DEFAULT NULL,
            `t_is_guard` int(11) DEFAULT '0' COMMENT '是否设置为守护礼物 0: 不是守护礼物 1: 守护礼物',*/

    @TableId
    private Integer tGiftId;

    private String tGiftName;

    private String tGiftGifUrl;

    private String tGiftStillUrl;

    private BigDecimal tGiftGold;

    private Integer tIsEnable;

    private Timestamp tCreateTime;

    private Integer tIsGuard;


}
