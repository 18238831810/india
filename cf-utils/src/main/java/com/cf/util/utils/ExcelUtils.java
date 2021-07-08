/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.util.utils;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author leek
 * @date 2020/4/3 16:43
 * @description excel工具类
 */
public class ExcelUtils {


    /**
     * Excel导出(包含字典)
     *
     * @param response      response
     * @param fileName      文件名
     */
    public static void exportExcelWithDict(HttpServletResponse response, String fileName,Collection<?> list,Class<?> pojoClass) throws Exception {
        //运营要求去除表头，所以此处设置fileName为null
        exportExcelWithDict(response,list,pojoClass);
    }

    /**
     * Excel导出(包含字典)
     *
     * @param response      response
     */
    public static void exportExcelWithDict(HttpServletResponse response,Collection<?> list,Class<?> pojoClass) throws Exception {
        ExportParams exportParams = new ExportParams(null, null);
        //引入自定义字典
        exportParams.setDictHandler(new MyExcelDictHandler());
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, list);
        MyExcelKeyValueUtil.export(response,null, workbook);
    }

    /**
     * Excel导出
     *
     * @param response      response
     * @param fileName      文件名
     * @param list          数据List
     * @param pojoClass     对象Class
     */
    public static void exportExcel(HttpServletResponse response, String fileName, Collection<?> list, Class<?> pojoClass) throws IOException {
        if(StringUtils.isBlank(fileName)){
            //当前日期
            fileName = DateUtil.date2String(new Date(),null);
        }

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(), pojoClass, list);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }

    /**
     * Excel导出，先sourceList转换成List<targetClass>，再导出
     *
     * @param response      response
     * @param fileName      文件名
     * @param sourceList    原数据List
     * @param targetClass   目标对象Class
     */
    public static void exportExcelToTarget(HttpServletResponse response, String fileName, Collection<?> sourceList, Class<?> targetClass) throws Exception {
        List targetList = new ArrayList<>(sourceList.size());
        for(Object source : sourceList){
            Object target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }

        exportExcel(response, fileName, targetList, targetClass);
    }

    /**
     * Excel导出
     */
    public static void exportExcel(HttpServletResponse response, String fileName, ExportParams entity, List<ExcelExportEntity> entityList, Collection<?> dataSet) throws IOException {
        if(StringUtils.isBlank(fileName)){
            //当前日期
            fileName = DateUtil.date2String(new Date(),null);
        }

        Workbook workbook = ExcelExportUtil.exportExcel( entity, entityList, dataSet);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
    }
}
