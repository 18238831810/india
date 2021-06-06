package com.cf.crs.common.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryPage {
    private Integer pageNum = 1;
    private Integer pageSize = 15;
}
