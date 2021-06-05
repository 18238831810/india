package com.binance.api.client.constant;

public enum OrderErrorEnum {
    ERROR_OVER_TIME(100, "超过时间，不能下单"),
    ERROR_NOT_BETWEEN(100, "下单区间值应该为[%s,%s]"),
    ERROR_PARAM(100, "参数有问题"),
    ERROR_GET_CAND(100, "已过下单时间，请稍后再试");
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
