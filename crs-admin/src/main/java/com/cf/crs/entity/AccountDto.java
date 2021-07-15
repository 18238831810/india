package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.cf.crs.common.entity.QueryPage;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户列表
 * @author frank
 * @date 20210-07-06
 *
 **/
@Data
public class AccountDto extends QueryPage implements Serializable  {

    @TableId
    @JsonProperty("tId")
    private Integer tId;

    @JsonProperty("tPhone")
    private String tPhone;

    @JsonProperty("tIdcard")
    private Integer tIdcard;

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

    /**
     * 直播收费标准（每分钟）
     */
    @JsonProperty("tFeeStandard")
    private Integer tFeeStandard;



}
