package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
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
@TableName("t_admin")
@Builder
@ApiModel("用户列表")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

    @TableId
    @JsonProperty("tId")
    private Integer tId;

    @JsonProperty("tUserName")
    private String tUserName;

    @JsonProperty("tPassWord")
    private String tPassWord;

    @JsonProperty("tRoleId")
    private String tRoleId;

    @JsonProperty("tIsDisable")
    private BigDecimal tIsDisable;

    @JsonProperty("tCreateTime")
    private Timestamp tCreateTime;



}
