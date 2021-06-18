package com.cf.crs.common.constant;

public enum OrderErrorEnum {
    ERROR_OVER_TIME(100, "msg_1012"),
    ERROR_NOT_BETWEEN(101, "msg_1013"),
    ERROR_PARAM(102, "msg_1014"),
    ERROR_NOT_ENOUGH(103, "msg_1015"),
    ERROR_NOT_FOUND(104, "msg_1016"),
    ERROR_NOT_MATCH(105, "msg_1017");
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
