package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 抽成设置
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@TableName("t_extract")
@Builder
@ApiModel("抽成设置")
@NoArgsConstructor
@AllArgsConstructor
public class ExtractEntity implements Serializable {
    /*
     `t_id` int(11) NOT NULL AUTO_INCREMENT,
  `t_project_type` int(11) DEFAULT NULL COMMENT '0.平台抽成比例\r\n1.一级主播推广比例\r\n2.二级主播推广比例\r\n3.一级用户推广比例\r\n4.二级用户推广比例\r\n5.视频聊天\r\n6.文字聊天\r\n7.查看手机号\r\n8.查看微信号\r\n9.查看私密照片\r\n10.查看私密视频',
  `t_extract_ratio` varchar(100) DEFAULT NULL,
  `t_create_time` datetime DEFAULT NULL,
  `explain` varchar(255) DEFAULT NULL COMMENT '解释说明',*/

    @TableId
    private Integer tId;

    private Integer tProjectType;

    private String tExtractRatio;

    private Timestamp tCreateTime;

    /**
     * 这个字段命名有毒，暂不查询
     */
    @TableField(exist = false)
    private String explain;

}
