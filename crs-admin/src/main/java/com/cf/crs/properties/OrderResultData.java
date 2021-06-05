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
public class OrderResultData implements Serializable {


    /**
     * 提交的order_sn
     */
    private String order_sn;

    /**
     * 平台生成的订单号
     */
    private String pt_order_sn;

    /**
     * 请跳转到此页面
     */
    private String pay_pageurl;


}
