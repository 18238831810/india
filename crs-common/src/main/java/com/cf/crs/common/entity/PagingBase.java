package com.cf.crs.common.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagingBase<T> implements Serializable {

    @ApiModelProperty(value = "列表")
    private List<T> list;

    @ApiModelProperty(value = "总条数")
    private long total;
}
