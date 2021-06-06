package com.binance.api.client.constant;

public enum OrderErrorEnum {
    ERROR_OVER_TIME(100, "超过时间，不能下单"),
    ERROR_NOT_BETWEEN(101, "下单区间值应该为[%s,%s]"),
    ERROR_PARAM(102, "参数有问题"),
    ERROR_NOT_ENOUGH(103, "余额不足"),
    ERROR_NOT_FOUND(104, "没有找到记录"),
    ERROR_NOT_MATCH(105, "过期的订单10分钟之内不允许撤单（请耐心等待，如10分钟之内未成交，可再撤销）");
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private String error;

    OrderErrorEnum(int code, String error) {
        this.code = code;
        this.error = error;
    }
}
