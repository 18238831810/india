package com.cf.util.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class OrderSignUtil {



    public static String createSign(String apiToken, Map<String, Object> paramMap) {
        String signSource = createSignSource(apiToken,paramMap);
        log.info("signSource:{}",signSource);
        //返回MD5加密
        return Md5Util.md5(signSource);
    }

    public static String createSignSource(String apiToken, Map<String, Object> paramMap) {
        // requestKey和nonceStr不参与字典序排序，直接放置在前面
        StringBuffer sb = new StringBuffer();

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
                log.info("sign value:{}",vObject.toString());
                sb.append("&").append(k).append("=").append(vObject.toString());
            }else{
                sb.append("&").append(k).append("=");
            }
        }
        // 第二步拼接秘钥
        sb.append("&key=").append(apiToken);
        return sb.toString().substring(1);
    }

}
