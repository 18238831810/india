package com.cf.util.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：frank
 * @description：数据导出转换规则
 * @date ：Created in 2020/6/15 18:54
 */
@Slf4j
public class MyExcelKeyValueUtil {

    private static Map<String, Map<String,String>> valueMap = new HashMap();

    private static Map<String,Integer> pMap = new HashMap();

    private static ThreadLocal<Map<String,String>> threadLocal = new ThreadLocal<Map<String,String>>();

    /**
     * 手机号、名称等隐藏部分字符标识
     */
    public static final String HIDEFLAG = "exportHide";

    static {
        initValues("k1", "1,UPI");
        initValues("k2", "0,未完成","1,失败","2,成功");
        initValues("k3", "1,UPI代付","3,IFSC代付");
        initValues("k4", "0,未审批","1,已审批");
        initValues("k5", "1,打赏主播");
        initValues("k6", "rise,涨","fall,跌","equal,平");
        initValues("k7", "0,刚生成","1,处理","-1,撤销订单");

        //手机号正则隐藏
        pMap.put("p1", 1);
        //姓名正则隐藏
        pMap.put("p2", 2);
        //身份证隐藏
        pMap.put("p3", 3);
    }


    public static void initValues(String key,String... arr ){
        HashMap<String, String> child = new HashMap<>();
        for (String strings : arr) {
            String[] split = strings.split(",");
            child.put(split[0],split[1]);
        }
        valueMap.put(key,child);
    }

    public static String getLabel(String pKey,Object realKey){
        if (realKey == null|| StringUtils.isEmpty(realKey.toString())) {
            return "";
        }
        if (pKey.startsWith("p")){
            String value = getValue(HIDEFLAG);
            if (!"true".equalsIgnoreCase(value)) return realKey.toString();
            //需要进行数据隐藏操作
            Integer type = pMap.get(pKey);
            if (type == null) return realKey.toString();
            if (type == 1) return hidePhone(realKey.toString());
            else if (type == 2) return hideName(realKey.toString());
            else if (type == 3) return hideIdCard(realKey.toString());
            else return realKey.toString();
        }
        String value = valueMap.get(pKey).get(String.valueOf(realKey));
        return value==null?"":value;
    }

    public static void setBaseParam(HttpServletRequest request){
        setBaseParam(request.getParameter(HIDEFLAG));
    }

    public static void setBaseParam(String hiddenInfo){
        if (StringUtils.isEmpty(hiddenInfo)) return;
        Map<String, String> map = getStringStringMap();
        map.put(HIDEFLAG,hiddenInfo);
        threadLocal.set(map);
    }

    public static void setKey(String key, String value){
        Map<String, String> map = getStringStringMap();
        map.put(key,value);
        threadLocal.set(map);
    }

    public static String getValue(String key){
        Map<String,String> map = threadLocal.get();
        if(map != null){
            if(map.containsKey(key) && map.get(key) != null){
                return map.get(key);
            }
        }
        return null;
    }

    public static void remove(){
        threadLocal.remove();
    }

    private static Map<String, String> getStringStringMap() {
        Map<String, String> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }


    public static String hidePhone(String phone){
        if (StringUtils.isEmpty(phone)) return "";
        phone = phone.trim();
        if (phone.length() == 9) return phone.replaceAll("(\\d{3})\\d{3}(\\d{3})", "$1****$2");
        else if(phone.length() > 9) return phone.replaceAll("(\\w{6}$)", "******");
        else return hideName(phone);
    }

    public static String hideName(String name){
        if (StringUtils.isEmpty(name)) return "";
        name = name.trim();
        if (name.length() >= 3) return name.replaceAll("^.|.$","*");
        return name.replaceAll("^.","*");
    }

    public static String hideIdCard(String idCard) {
        if (StringUtils.isEmpty(idCard)) return "";
        idCard = idCard.trim();
        if (idCard.length() >= 10) return idCard.replaceAll("(\\w{10}$)", "********");
        else return idCard.replaceAll("(\\w{1}$)", "********");
    }

    public static String hideBankCard(String bankCard) {
        if (StringUtils.isEmpty(bankCard)) return "";
        bankCard = bankCard.trim();
        if (bankCard.length() >= 10) return bankCard.replaceAll("(\\w{10}$)", "******");
        else return bankCard.replaceAll("(\\w{1}$)", "******");
    }

    public static void export(HttpServletResponse response, String fileName, Workbook workbook) throws IOException {
        try {
            response.reset();
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName + ".xls", "utf-8"));
            response.setHeader("Access-Control-Allow-Origin", "*");
            ServletOutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }


}

    