package com.esserver.common.utils;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.esserver.config.util.XStreamUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 工具类
 * 
 * @author xubincheng
 *
 */
@Component
public class CommonUtil {

	/**
	 * 方法描述：判断字符串是否是日期格式，默认按照yyyy-MM-dd HH:mm:ss格式转换，失败后
	 * @param str
	 * @return 
	 */
	public static boolean isValidDate(String str) {
		return isValidDate(str, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 方法描述：判断字符串是否是有效指定的日期格式的日期字符串
	 * @param str 日期字符串
	 * @param format 日期格式
	 * @return
	 */
	public static boolean isValidDate(String str, String format) {
		boolean convertSuccess = true;
		// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			// 设置lenient为false.
			// 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
			sdf.setLenient(false);
			sdf.parse(str);
		} catch (ParseException e) {
			convertSuccess = false;
		}
		return convertSuccess;
	}
	
	/**
	 * 获取当前时间字符串，默认格式 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurrTime() {
		return getCurrTime("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 根据指定日期格式获取当前时间字符串
	 * @param format
	 * @return
	 */
	public static String getCurrTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
	
	/**
	 * 判断是否是空集合
	 * @param list
	 * @return
	 */
	public static boolean isEmptyList(List<?> list) {
		if (list==null || list.size()>0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断是否不是空集合
	 * @param list
	 * @return
	 */
	public static boolean isNotEmptyList(List<?> list) {
		return !isEmptyList(list);
	}
	
	
	
}
