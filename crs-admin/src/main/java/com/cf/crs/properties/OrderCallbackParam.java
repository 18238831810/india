package com.cf.crs.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * 代收款回调参数
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
public class OrderCallbackParam implements Serializable {


    /**
     * 商户单号 商户自己系统的业务单号
     */
    private String order_sn;

    /**
     * 平台单号 支付平台的单号
     */
    private String pt_order_sn;

    /**
     * 金额 注意！！！
     * 用户实际支付的金额，不一定与订单金额一致，务必按照此回调金额进行上分，并更新贵司系统的订单金额
     */
    private Float amount;

    /**
     * 支付完成时间 支付完成时间戳
     */
    private Long time;

    /**
     * 支付状态 1:成功,其他状态请忽略;
     * 注意：请一定要处理成功的回调，并给用户上分
     */
    private Integer status;

    /**
     * md5签名	参照最下方加签方式
     */
    private String sign;

}
