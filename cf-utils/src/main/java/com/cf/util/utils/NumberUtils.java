package com.cf.util.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 
 * @ClassName:  NumberUtils   
 * @Description:数字处理工具类 
 * @author: spark
 * @date:   Apr 10, 2019 3:51:28 PM   
 *
 */
public class NumberUtils {

	/**
	 * 功能：转换成2位小数
	 */
	public static String to2Decimal(BigDecimal srcData) {
		if(null == srcData){
			return "0.00";
		}
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatter.format(srcData);
	}
	
	/**
	 * 功能：转换成3位小数
	 */
	public static String to3Decimal(BigDecimal srcData) {
		if(null == srcData){
			return "0.000";
		}
		NumberFormat formatter = new DecimalFormat("#0.000");
		return formatter.format(srcData);
	}
	
	/**
	 * 功能：转换成N位小数
	 */
	public static String toDecimal(BigDecimal srcData, int decimalPoint) {
		StringBuffer decimalFormat = new StringBuffer();
		decimalFormat.append("0.0");
		for(int a = 1; a < decimalPoint; a++){
			decimalFormat.append("0");
		}
		String strDcFormat = decimalFormat.toString();
		if(null == srcData){
			return strDcFormat;
		}		
		NumberFormat formatter = new DecimalFormat("#"+strDcFormat);
		return formatter.format(srcData);
	}
	
	/**
	 * 功能：转换成N位小数，若等于0，则显示为空
	 */
	public static String toDecimalNOrBlank(BigDecimal srcData, int decimalPoint) {
		if(srcData == null || srcData.equals(BigDecimal.ZERO)){
			return "";
		}
		StringBuffer decimalFormat = new StringBuffer();
		decimalFormat.append("0.0");
		for(int a = 1; a < decimalPoint; a++){
			decimalFormat.append("0");
		}
		String strDcFormat = decimalFormat.toString();	
		NumberFormat formatter = new DecimalFormat("#"+strDcFormat);
		return formatter.format(srcData);
	}

	public static BigDecimal add(Object value1, Object value2){
		//boolean result = validateParam(value1, value2);
		BigDecimal b1 = new BigDecimal(String.valueOf(value1));
		BigDecimal b2 = new BigDecimal(String.valueOf(value2));
		return b1.add(b2);
	}

	private static boolean validateParam(Object value1, Object value2){
		System.out.println("参与计算参数：value1="+value1+",value2="+value2);
		boolean validateResult = false;
		if(value1==null||value2==null){
			System.out.println("参与计算参数有空值!");
			return false;
		}

		if((value1 instanceof Integer||value1 instanceof Double
				||value1 instanceof Float||value1 instanceof Long)&&
				(value2 instanceof Integer||value2 instanceof Double
						||value2 instanceof Float||value2 instanceof Long)){
			validateResult = true;
		}
		return validateResult;
	}
}
