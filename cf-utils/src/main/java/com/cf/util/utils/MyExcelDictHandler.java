package com.cf.util.utils;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;

import java.util.Objects;

/**
 * @author ：frank
 * @description：数据导出字典(自定义数据转换规则)
 * @date ：Created in 2020/6/15 18:54
 */
public class MyExcelDictHandler implements IExcelDictHandler {

    /**
     * 时间戳转换规则标识
     */
    private final static String TIME_RULE = "t_";

    /**
     * MyExcelKeyValueUtil数据转换规则标识
     */
    private final static String DEFINE_RULE = "p_";

    /**
     * 自定义数据转换规则
     * {
     * 1. t_yyyy-MM-dd  (t_开始代表走时间戳转换规则，t_后面的字符串代表转换格式)
     * 2. p_ (p_开始走MyExcelKeyValueUtil中定义的规则，p_后面是规则字段，具体规则可参考MyExcelKeyValueUtil进行匹配和自定义)
     * }
     *
     * @param dict
     * @param obj
     * @param name
     * @param value
     * @return
     */
    @Override
    public String toName(String dict, Object obj, String name, Object value) {
        if (dict.startsWith(TIME_RULE)) {
            //时间转换规则(long转str)
            String ruleStr = dict.substring(2);
            if (Objects.isNull(value)) return null;
            long dateValue = DataChange.obToLong(value);
            if (dateValue != 0L) {
                return DateUtil.timesToDate(dateValue, ruleStr);
            }
            return null;
        } else if (dict.startsWith(DEFINE_RULE)) {
            //获取数据处理规则字段
            String ruleStr = dict.substring(2);
            return MyExcelKeyValueUtil.getLabel(ruleStr, value);
        }
        return DataChange.obToString(value);
    }

    @Override
    public String toValue(String dict, Object obj, String name, Object value) {
        return DataChange.obToString(value);
    }
}

    