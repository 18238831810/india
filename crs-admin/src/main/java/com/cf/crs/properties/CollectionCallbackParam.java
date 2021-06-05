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
public class CollectionCallbackParam implements Serializable {


    /**
     * 商户单号 商户自己系统的业务单号
     */
    private String order_sn;

    /**
     * 平台单号 支付平台的单号
     */
    private String pt_order_sn;

    /**
     * 和提交时金额一致
     */
    private Float amount;

    /**
     * 支付完成时间 支付完成时间戳
     */
    private Long time;

    /**
     * 2成功，-2失败（收款人账户有误）（如果是其他状态，请忽略此回调）
     */
    private Integer status;

    /**
     * 额外信息 订单失败后的失败原因
     */
    private String msg;

    /**
     * md5签名	参照最下方加签方式
     */
    private Integer sign;

}
