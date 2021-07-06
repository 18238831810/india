package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 用户列表
 * @author frank
 * @date 20210-07-06
 *
 **/
@Data
@TableName("u_menu")
@Builder
@ApiModel("菜单列表")
@NoArgsConstructor
@AllArgsConstructor
public class MenuEntity implements Serializable {

    @TableId
    private Integer id;

    private String name;

    private String url;

    private String remark;

    private Integer type;

    private Integer parentId;

    private Long createdTime;

    private Long updatedTime;


    /*CREATE TABLE `cf_menu` (
            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
            `name` varchar(32) DEFAULT NULL COMMENT '名称',
            `url` varchar(64) DEFAULT NULL COMMENT '地址',
            `remark` varchar(128) DEFAULT NULL COMMENT '介绍',
            `type` int(2) DEFAULT '0' COMMENT '(类型，0:菜单  1:按钮)',
            `parent_id` int(11) DEFAULT NULL COMMENT '父级id',
            `created_time` bigint(13) DEFAULT '0' COMMENT '创建时间',
            `updated_time` bigint(13) DEFAULT '0' COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='菜单信息';*/



}
