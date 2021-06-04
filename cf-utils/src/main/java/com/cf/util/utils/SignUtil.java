package com.cf.util.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.*;

public class SignUtil {


    public static void main(String[] args) {
        Map<String, Object> customParams = new HashMap<>();
        customParams.put("a2", "bbb");
        customParams.put("a1", "aaa");
        String sign = createSign("dsvf86aa2913fbf4b7", "bef8ac", "eed1a05798e341d48e84b4c70ce4ab36", customParams);
        System.out.println(sign); // 控制台打印出sign值为7B22211FF0B73AA5E1112E311F75687D
    }

    public static String createSign(String secretId, String nonceStr, String secret, Map<String, Object> paramMap) {
        String signSource = createSignSource(secretId, nonceStr, secret, paramMap);
        //返回MD5加密，字符转换为大写
        return DigestUtils.md5DigestAsHex(signSource.getBytes()).toUpperCase();
    }

    public static String createSignSource(String secretId, String nonceStr, String secret, Map<String, Object> paramMap) {
        // requestKey和nonceStr不参与字典序排序，直接放置在前面
        StringBuffer sb = new StringBuffer();
        sb.append("secretId=").append(secretId);
        sb.append("&nonceStr=").append(nonceStr);

        // 根据规则创建可排序的map集合,参数按照参数名ASCII码从小到大排序（字典序）
        SortedMap<String, Object> params = new TreeMap<>();
        Map<String, Object> body = paramMap;
        if (body != null) {
            for (Map.Entry<String, Object> entry : body.entrySet()) {
                params.put(entry.getKey(), entry.getValue());
            }
        }

        // 字典序
        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String k = entry.getKey();
            Object vObject = entry.getValue();

            //为空不参与签名、参数名区分大小写
            if (null != vObject && StringUtils.isNotBlank(vObject.toString()) && StringUtils.isNotBlank(k)) {
                sb.append("&").append(k).append("=").append(vObject.toString());
            }
        }

        // 第二步拼接秘钥
        sb.append("&secretKey=").append(secret);
        // System.out.println("sign source: " + sb.toString());
        //返回MD5加密，字符转换为大写
        return sb.toString();
    }

}
