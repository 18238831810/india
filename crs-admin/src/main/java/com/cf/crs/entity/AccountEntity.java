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

/**
 * 用户列表
 * @author frank
 * @date 20210-07-06
 *
 **/
@Data
@TableName("t_user")
@Builder
@ApiModel("用户列表")
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity implements Serializable {

    @TableId
    private Integer tId;

    @JsonProperty("tPhone")
    private String tPhone;





}
