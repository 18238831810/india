package com.cf.crs.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 代收款参数
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
public class CollectionResultData implements Serializable {

    /*order_sn:"*********", # 提交的order_sn
        pt_order_sn:"**************"# 平台生成的订单号*/

    /**
     * 提交的order_sn
     */
    private String order_sn;

    /**
     * 平台生成的订单号
     */
    private String pt_order_sn;


}
