package com.ruoyi.common.utils.number;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;

/**
 * 
 * @描述: Object值转为不同类型的数值.
 * @version 1.0
 * 
 */
public class NumberUtil {
	
	/**
	 * 判断对象值是否为纯数字组成(如:001234,7899),只有纯数字的值才能转Long或Integer.
	 * @param obj 要判断的值.
	 * @return true or false.
	 */
	public static boolean isDigits(Object obj) {
		if (obj == null){
			return false;
		}
		return NumberUtils.isDigits(obj.toString());
	}
	
	/**
	 * 判断对象值是否为数字,包含整数和小数(如:001234,7899,7.99,99.00)
	 * @param obj 要判断的值.
	 * @return true or false.
	 */
	public static boolean isNumber(Object obj) {
		if (obj == null){
			return false;
		}
		return NumberUtils.isNumber(obj.toString());
	}

	/**
	 * Object对象转BigDecimal <br/>
	 * 1、如果Object为空或Object不是数值型对象:抛数字格式化异常 <br/>
	 * 2、Object为数值型对象:转为BigDecimal类型并返回 <br/>
	 * @param obj 要转换的Object对象 <br/>
	 * @return BigDecimal
	 */
	public static BigDecimal toBigDecimal(Object obj) {
		if (obj == null || !NumberUtils.isNumber(obj.toString())){
			throw new NumberFormatException("数字格式化异常");
		} else {
			return new BigDecimal(obj.toString());
		}
	}
	
	/**
	 * Object对象转Double <br/>
	 * 1、如果Object为空或Object不是数值型对象:抛数字格式化异常 <br/>
	 * 2、Object为数值型对象:转为Double类型并返回 <br/>
	 * @param obj 要转换的Object对象 <br/>
	 * @return Double
	 */
	public static Double toDouble(Object obj) {
		if (obj == null || !NumberUtils.isNumber(obj.toString())){
			throw new NumberFormatException("数字格式化异常");
		} else {
			return Double.valueOf(obj.toString());
		}
	}
	
	/**
	 * Object对象转Long <br/>
	 * 1、如果Object为空或Object不是整数型对象:抛数字格式化异常 <br/>
	 * 2、Object为整数型对象:转为Long类型并返回 <br/>
	 * @param obj 要转换的Object对象 <br/>
	 * @return Long
	 */
	public static Long toLong(Object obj) {
		if (obj == null || !NumberUtils.isDigits(obj.toString())){
			throw new NumberFormatException("数字格式化异常");
		} else {
			return Long.valueOf(obj.toString());
		}
	}
	
	/**
	 * Object对象转Integer <br/>
	 * 1、如果Object为空或Object不是整数型对象:抛数字格式化异常 <br/>
	 * 2、Object为整数型对象:转为Integer类型并返回 <br/>
	 * @param obj 要转换的Object对象 <br/>
	 * @return Integer
	 */
	public static Integer toInteger(Object obj) {
		if (obj == null || !NumberUtils.isDigits(obj.toString())){
			throw new NumberFormatException("数字格式化异常");
		} else {
			return Integer.valueOf(obj.toString());
		}
	}
	
	
//	public static void main(String[] args) {
//		BigDecimal bdcm4 = toBigDecimal("9999");
//		System.spout.println("bdcm4:" + bdcm4);
//	}

}
