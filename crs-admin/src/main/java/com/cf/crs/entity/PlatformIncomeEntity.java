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
@TableName("t_platform_income")
@Builder
@ApiModel("平台收益 视频直播金币收益表")
@NoArgsConstructor
@AllArgsConstructor
public class PlatformIncomeEntity implements Serializable {
    /*
     CREATE TABLE `t_platform_income` (
  `t_id` int(11) NOT NULL,
  `t_gold` decimal(18,2) DEFAULT NULL,
  PRIMARY KEY (`t_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='平台收益 视频直播金币收益表';*/

    @TableId
    private Integer tId;

    private BigDecimal tGold;

}
