package com.cf.util.utils;

/**
 * @author frank
 * 国家化标识
 * 2020/08/13
 */
public enum LocalEnum {

    CN("cn","zh_CN"),
    VN("vn","vi_VN"),
    EN("en","en_US"),
    IN("in","en_IN"),
    ZH_CN("zh_CN","zh_CN"),
    VI_VN("vi_VN","vi_VN"),
    EN_US("en_US","en_US"),
    EN_IN("en_IN","en_IN");


	private String code;

    private String local;

    LocalEnum(String code, String local) {
        this.code = code;
        this.local = local;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }


    public static LocalEnum get(String code){
        for(LocalEnum u : LocalEnum.values()){
            if(u.getCode().equals(code)){
                return u;
            }
        }
        return EN;
    }

}