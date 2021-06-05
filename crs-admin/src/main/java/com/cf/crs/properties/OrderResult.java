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
public class OrderResult<T extends Serializable> implements Serializable {

    /**
     * 商户id
     */
    private Integer code;

    /**
     * 支付类型 1:UPI（目前只能为1）
     */
    private String msg;

    /**
     * 订单号 32字符以内，不能重复
     */
    private T data;

    public boolean isOk(){
        return this.code != null && this.code.equals(1);
    }

}
