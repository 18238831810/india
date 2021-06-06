package com.cf.crs.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PagingBase<T> implements Serializable {
    private List<T> list;
    private long total;
}
