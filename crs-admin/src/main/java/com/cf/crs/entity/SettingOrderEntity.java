package com.cf.crs.entity;

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
@TableName("u_setting_order")
public class SettingOrderEntity {


    @ApiParam(hidden = true)
    private Integer id;

    /**
     * 下单总值
     */
    @ApiParam(name = "payment", value = "下单总量")
    private Double payment;

    /**
     * 买涨跌或平这三个方向
     * rise fall   equal
     */
    @ApiParam(name = "buyDirection", allowableValues = "rise,fall,equal", value = "下单方向")
    private String buyDirection;

    @ApiParam(name = "name", value = "用户昵称")
    private String name;

    /*CREATE TABLE `u_setting_order` (
            `id` int(11) NOT NULL AUTO_INCREMENT,
            `payment` float DEFAULT NULL COMMENT '下单总值',
            `buy_direction` varchar(20) DEFAULT NULL COMMENT '买涨跌或平这三个方向',
            `name` varchar(50) DEFAULT NULL COMMENT '用户昵称',
    PRIMARY KEY (`id`) USING BTREE,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/

}
