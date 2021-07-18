package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    @JsonProperty("tId")
    private Integer tId;

    @JsonProperty("tPhone")
    private String tPhone;

    @JsonProperty("tIdcard")
    private Integer tIdcard;

    @TableField("t_nickName")
    @JsonProperty("tNickName")
    private String tNickName;

    @JsonProperty("tSex")
    private Integer tSex;

    @JsonProperty("tAge")
    private Integer tAge;

    @JsonProperty("tRole")
    private Integer tRole;

    @JsonProperty("tDisable")
    private Integer tDisable;

    @JsonProperty("tRecord")
    private Integer tRecord;

    @JsonProperty("tReferee")
    private Long tReferee;

    /**
     * 直播收费标准（每分钟）
     */
    @JsonProperty("tFeeStandard")
    private BigDecimal tFeeStandard;


    /**
     * 直播主播分层比例
     */
    @JsonProperty("tFeeRadio")
    private Integer tFeeRadio;




}
