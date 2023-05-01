package com.ruoyi.common.utils.clazz;


import com.alibaba.fastjson.JSON;
import com.ruoyi.common.utils.JSONUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 类工具
 * @version  v 1.0
 */
public class ClassUtil {
	private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);
	/**
	 * 基本数据类型
	 */
	private static String[] baseType = {
			"string",
			"int",
			"integer",
			"boolean",
			"float",
			"long",
			"byte",
			"double",
			"short",
			"char",
			"date",
			"timestamp",
			"java.lang.Integer",
			"java.lang.Float",
			"java.lang.Long",
			"java.lang.Byte",
			"java.lang.Double",
			"java.lang.Short",
			"java.lang.Character",
			"java.lang.Boolean",
			"java.sql.Timestamp" };
	public static String[] getBaseType()
	{
		return baseType;
	}

	/**
	 *
	 * 判断是否基本数据类型
	 * @Title: isBaseClass
	 * @param clazz
	 * @return
	 * @Return: boolean 返回值
	 */
	public static boolean isBaseType(Class clazz) {
		String name = clazz.getName();
		for (int i = 0; i < baseType.length; i++) {
			if (name.indexOf(baseType[i]) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * 判断是否基本数据类型
	 * @Title: isBaseClass
	 * @param object
	 * @return
	 * @Return: boolean 返回值
	 */
	public static boolean isBaseType(Object object) {
		Class clazz = object.getClass();
		return isBaseType(clazz);
	}
	/**
	 *
	 * 判断是否基本数据类型
	 * @Title: isBaseClass
	 * @param clazz
	 * @return
	 * @Return: boolean 返回值
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isBaseClass(Class clazz) {
		for (int i = 0; i < baseType.length; i++) {
			if (baseType[i].equals(clazz.getName()))
				return true;
		}
		return false;
	}

	/**
	 *
	 * 字符串转数字类型
	 * @Title: strToNumber
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: T 返回值
	 */
	@SuppressWarnings("unchecked")
	public static  Number strToNumber(String str,boolean isDefaultValue) throws ParseException{
		Number obj = null;
		if (StringUtils.isNotBlank(str)) {
			obj = NumberFormat.getInstance().parse(str);
		}else if(isDefaultValue){
			obj = NumberFormat.getInstance().parse("0");
		}
		return  obj;
	}

	private static BigDecimal strToBigDecimal(String str, boolean isDefaultValue) {
		BigDecimal obj = null;
		if (StringUtils.isNotBlank(str)) {
			obj = new BigDecimal(str);
		}else if(isDefaultValue){
			obj = new BigDecimal("0");
		}
		return  obj;
	}

	/**
	 *
	 * 字符串转int
	 * @Title: strToInteger
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Integer 返回值
	 */
	public static Integer strToInteger(String str, boolean isDefaultValue) throws ParseException {
		Integer obj = null;
		if (StringUtils.isNotBlank(str)) {
			obj = Integer.valueOf(NumberFormat.getInstance().parse(str).intValue());
		} else if (isDefaultValue) {
			obj = Integer.valueOf(0);
		}
		return obj;
	}

	/**
	 *
	 * 字符串转Date
	 * @Title: strToDate
	 * @param str
	 * @return
	 * @throws ParseException
	 * @Return: Date 返回值
	 */
	public static Date strToDate(String str) throws ParseException{
		if (StringUtils.isBlank(str) || str.equals("null")) {
			return null;
		}
		if(str.indexOf("T") != -1){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			return format.parse(str);
		}else if(str.indexOf("-") != -1){
			SimpleDateFormat df1 = null;
			if (str.length() < 11) {
				df1 = new SimpleDateFormat("yyyy-MM-dd");
			} else if (str.length() < 13) {
				df1 = new SimpleDateFormat("yyyy-MM-dd HH");
			}else if (str.length() < 17) {
				df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			}else  {
				df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			return df1.parse(str);
		}else{
			return new Date(Long.valueOf(str));
		}
	}

	/**
	 *
	 * 字符串转Float
	 * @Title: strToFloat
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Float 返回值
	 */
	public static Float strToFloat(String str,boolean isDefaultValue) throws ParseException{
		Float obj = null;
		if (StringUtils.isNotBlank(str)) {
			if (str.endsWith("%")) {
				obj = Float.valueOf(NumberFormat.getPercentInstance().parse(str).floatValue());
			} else {
				obj = Float.valueOf(NumberFormat.getInstance().parse(str).floatValue());
			}
		}else if(isDefaultValue){
			obj = Float.valueOf(0);
		}
		return obj;
	}

	/**
	 *
	 * 字符串转Long
	 * @Title: strToLong
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Long 返回值
	 */
	public static Long strToLong(String str,boolean isDefaultValue) throws ParseException{
		Long obj = null;
		if (StringUtils.isNotBlank(str)) {
			obj = Long.valueOf(NumberFormat.getInstance().parse(str).longValue());
		}else if(isDefaultValue){
			obj = Long.valueOf(0);
		}
		return obj;
	}
	/**
	 *
	 * 字符串转byte
	 * @Title: strToByte
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: byte 返回值
	 */
	public static Byte strToByte(String str,boolean isDefaultValue) throws ParseException{
		Byte obj = null;
		if (StringUtils.isNotBlank(str)) {
			if("false".equals(str)){
				obj = 0;
			}else if("true".equals(str)){
				obj = 1;
			}else {
				obj = Byte.valueOf(NumberFormat.getInstance().parse(str).byteValue());
			}
		}else if(isDefaultValue){
			obj = 0;
		}
		return obj;
	}

	/**
	 *
	 * 字符串转    Double
	 * @Title: strToDouble
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Double 返回值
	 */
	public static Double strToDouble(String str,boolean isDefaultValue) throws ParseException{
		Double obj = null;
		if (StringUtils.isNotBlank(str)){
			if (str.endsWith("%")) {
				obj = Double.valueOf(NumberFormat.getPercentInstance().parse(str).doubleValue());
			} else {
				obj = Double.valueOf(NumberFormat.getInstance().parse(str).doubleValue());
			}
		}else if(isDefaultValue){
			obj = Double.valueOf(0.0D);
		}
		return obj;
	}

	/**
	 *
	 * 字符串转  Short
	 * @Title: strToShort
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Short 返回值
	 */
	public static Short strToShort(String str,boolean isDefaultValue) throws ParseException{
		Short obj = null;
		if (StringUtils.isNotBlank(str)) {
			obj = Short.valueOf(NumberFormat.getInstance().parse(str).shortValue());
		}else if(isDefaultValue){
			obj = 0;
		}
		return obj;
	}

	/**
	 *
	 * 字符串转Character
	 * @Title: strToCharacter
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Character 返回值
	 */
	public static Character strToCharacter(String str,boolean isDefaultValue) throws ParseException{
		Character obj = null;
		if(StringUtils.isNotBlank(str)){
			obj = new Character(str.charAt(0));
		}else if (isDefaultValue) {
			obj = 0;
		}
		return obj;
	}

	/**
	 *
	 * 字符串转   Boolean
	 * @Title: strToBoolean
	 * @param str
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: Boolean 返回值
	 */
	public static Boolean strToBoolean(String str,boolean isDefaultValue) throws ParseException{
		Boolean obj = null;
		if(StringUtils.isNotBlank(str)){
			if (str.toLowerCase().equals("true") || str.equals("1")) {
				obj = Boolean.valueOf(true);
			}else if (str.toLowerCase().equals("false") || str.equals("0")) {
				obj = Boolean.valueOf(false);
			}
		}else if(isDefaultValue){
			obj = false;
		}
		return obj;
	}

	public static String objToString(Object obj){
		return objToString(obj,false);
	}

	/**
	 *
	 * obj类型转字符串类型
	 * @Title: objToString
	 * @param obj
	 * @param isDefaultValue isDefaultValue如果为空是否返回默认值
	 * @return
	 * @Return: String 返回值
	 */
	public static String objToString(Object obj,boolean isDefaultValue){
		String str = null;

		if(obj == null){
			if(isDefaultValue){
				str = "";
			}
		}else{
			str = String.valueOf(obj);
		}
		return str;
	}

	/**
	 *
	 * obj 对象转list对象
	 * @param <T>
	 * @Title: objToList
	 * @param obj
	 * @param isDefaultValue
	 * @param clazz
	 * @return
	 * @throws ParseException
	 * @Return: List 返回值
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public static <T> List<T> objToList(Object obj,boolean isDefaultValue,Class<T> clazz) {
		List list = null;
		try {
			if (obj == null){
				if(isDefaultValue){
					list = new ArrayList();
				}
			}else if (obj instanceof List) {
				List listObj = (List) obj;
				list = new ArrayList(listObj.size());
				for(Object changeObj : listObj){
					list.add(formatObject(changeObj,isDefaultValue, clazz));
				}
			}else if(obj.getClass().isArray()){
				list = arrayToList((Object[]) obj, isDefaultValue, clazz);
			}else if (JSONUtils.mayBeJSONArray(obj)) {
				List temp = JSON.parseArray(obj.toString());
				list = arrayToList(temp == null ? null : temp.toArray(), isDefaultValue, clazz);
			} else {
				String temp = obj.toString();
				list = arrayToList(temp.split(","), isDefaultValue, clazz);
			}
		}  catch (ParseException e) {
			logger.error("类型转换错误{}",e);
		}
		return list;
	}
	/**
	 *
	 * 将字符串转换为指定类型
	 * @Title: format
	 * @param clazz 指定类型
	 * @param str 要转换的字符类
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: T 返回值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T format(String str,boolean isDefaultValue,Class<T> clazz) throws ParseException {
		Object obj = null;
		String name = clazz.getName();
		if (name.equals("java.lang.Number")) {
			obj = strToNumber(str, isDefaultValue);
		}else if(name.equals("java.math.BigDecimal")) {
			obj = strToBigDecimal(str,isDefaultValue);
		}else if ((name.equals("int")) || (name.equals("java.lang.Integer"))) {
			obj = strToInteger(str, isDefaultValue);
		} else if (name.equals("java.lang.String")) {
			if(str == null && isDefaultValue){
				obj = "";
			}else{
				obj = str;
			}
		} else if ((name.equals("java.util.Date")) || (name.equals("java.sql.Date"))) {
			obj = strToDate(str);
		} else if ((name.equals("float")) || (name.equals("java.lang.Float"))) {
			strToFloat(str, isDefaultValue);
		} else if ((name.equals("long")) || (name.equals("java.lang.Long"))) {
			obj = strToLong(str, isDefaultValue);
		} else if ((name.equals("byte")) || (name.equals("java.lang.Byte"))) {
			obj = strToByte(str, isDefaultValue);
		} else if ((name.equals("double")) || (name.equals("java.lang.Double"))) {
			obj = strToDouble(str, isDefaultValue);
		} else if ((name.equals("short")) || (name.equals("java.lang.Short"))) {
			obj = strToShort(str, isDefaultValue);
		} else if ((name.equals("char")) || (name.equals("java.lang.Character"))) {
			obj = strToCharacter(str, isDefaultValue);
		} else if ((name.equals("boolean")) || (name.equals("java.lang.Boolean"))) {
			obj = strToBoolean(str, isDefaultValue);
		}
		return (T) obj;
	}

	/**
	 *
	 * Obj类型转换为指定类型  ,不返回默认值
	 * @Title: formatObject
	 * @param clazz
	 * @param obj
	 * @return
	 * @throws ParseException
	 * @Return: T 返回值
	 */
	public static <T> T formatObject(Object obj,Class<T> clazz){
		return formatObject(obj, false, clazz);
	}

	/**
	 *
	 * Obj类型转换为指定类型
	 * @Title: formatObject
	 * @param clazz
	 * @param obj
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @return
	 * @throws ParseException
	 * @Return: T 返回值
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T formatObject(Object obj, boolean isDefaultValue,Class<T> clazz)  {
		try {
			if (clazz == null){
				return null;
			}else if (clazz.isInstance(obj)){
				return (T) obj;
			}if(List.class.isAssignableFrom(clazz)){ //判断是否为List
				return (T) objToList(obj, isDefaultValue, Object.class);
			}else if (clazz.isArray()) {
				Class subclazz = clazz.getComponentType();
				if (isBaseType(subclazz)) {// 只支持基础类型转换
					Object returmValue = null;
					if (obj != null) {
						List list = objToList(obj, isDefaultValue, subclazz);
						returmValue = Array.newInstance(subclazz, list.size());
						for (int i = 0; i < list.size(); i++) {
							Array.set(returmValue, i, formatObject(list.get(i), isDefaultValue, subclazz));
						}
					} else if (isDefaultValue) {
						returmValue = Array.newInstance(subclazz, 0);
					} else {
						returmValue = null;
					}
					return (T) returmValue;
				}else if(obj instanceof List){
					List list = (List) obj;
					Object returmValue = Array.newInstance(subclazz, list.size());
					for (int i = 0; i < list.size(); i++) {
						Array.set(returmValue, i, formatObject(list.get(i), isDefaultValue, subclazz));
					}
					return (T) returmValue;
				}
				throw new IllegalArgumentException ("only base type can change to Array");
			}else if (obj == null) {
				return format((String) obj, isDefaultValue, clazz);
			}else if (obj instanceof Double) {
				Double doubleStr = (Double) obj;
				return format(BigDecimal.valueOf(doubleStr.doubleValue()).toPlainString(), isDefaultValue, clazz);
			}else if (obj instanceof BigDecimal) {
				BigDecimal doubleStr = (BigDecimal) obj;
				return format(doubleStr.toPlainString(), isDefaultValue, clazz);
			}else if(obj instanceof Calendar){
				Calendar calendar = (Calendar) obj;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return format(sdf.format(calendar.getTime()), isDefaultValue, clazz);
			}else if(obj instanceof List){
				obj = StringUtils.join(((List)obj).toArray(),",");
			}
			return format(obj.toString(), isDefaultValue, clazz);
		} catch (ParseException ex) {
			logger.error("类型转换错误{}",ex);
			return null;
		}
	}

	/**
	 * 指定类型列表转换
	 * @Title: format
	 * @param obj
	 * @param isDefaultValue 如果为空是否返回默认值
	 * @param clazz
	 * @return
	 * @throws ParseException
	 * @Return: List<T> 返回值
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> arrayToList( Object[] obj,boolean isDefaultValue,Class<T> clazz) throws ParseException {
		if(obj == null) return null;
		if(obj.getClass().getName().equals(clazz.getName())){
			return new ArrayList<T>(Arrays.asList((T) obj));
		}
		List o = new ArrayList(obj.length);
		for (int i = 0; i < obj.length; i++) {
			o.add(formatObject(obj[i],isDefaultValue, clazz));
		}
		return o;
	}

	/**
	 *
	 * 是否指定类型
	 * @Title: isClassType
	 * @param o
	 * @param className
	 * @return
	 * @Return: boolean 返回值
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isClassType(Object o, String className) {
		try {
			Class clazz = Class.forName(className);
			return clazz.isInstance(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * 是否指定类型
	 * @Title: isClassType
	 * @param clazz
	 * @param className
	 * @return
	 * @Return: boolean 返回值
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isClassType(Class clazz, String className) {
		String name = clazz.getName();
		return name.equals(className);
	}

}
