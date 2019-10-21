package com.esserver.datasync.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.MapUtils;

import com.esserver.config.application.vo.ESApplication;

public class CounterUtil {
	private static Map<String, AtomicInteger> counterMap;
	private static Map<String, Integer> totalCountMap; 
	static {
		counterMap = Collections.synchronizedMap(new HashMap<>());//使用线程安全的map：synchronizedMap
		totalCountMap = Collections.synchronizedMap(new HashMap<>());
	}
	private CounterUtil(){
		
	}
	
	/**
	 * 创造全量数据计数器
	 * @param key appCode + indexCode
	 * @param totalCount
	 */
	public static void setFullDataCounter(ESApplication app, int totalCount) {
		String key = app.getAppCode() + "-" + app.getIndexCode() + "-FullData";
		AtomicInteger atomicInteger = new AtomicInteger(0);
		counterMap.put(key, atomicInteger);
		totalCountMap.put(key, totalCount);
	}
	
	/**
	 * 获取全量计数器
	 * @param app
	 * @return
	 */
	public static AtomicInteger getFullDataCounter(ESApplication app) {
		String key = app.getAppCode() + "-" + app.getIndexCode() + "-FullData";
		if (counterMap.containsKey(key)) {
			return counterMap.get(key);
		}
		return null;
	}
	
	/**
	 * 获取全量同步总数
	 * @param app
	 * @return
	 */
	public static int getFullDataTotalCount(ESApplication app) {
		String key = app.getAppCode() + "-" + app.getIndexCode() + "-FullData";
		if (totalCountMap.containsKey(key)) {
			return totalCountMap.get(key);
		}
		return 0;
	}
	
	
	
	
	
	/**
	 * 创造增量数据计数器
	 * @param key appCode + indexCode
	 * @param totalCount
	 */
	public static void setIncreDataCounter(ESApplication app, int totalCount) {
		String key = app.getAppCode() + "-" + app.getIndexCode() + "-FullData";
		AtomicInteger atomicInteger = new AtomicInteger(0);
		counterMap.put(key, atomicInteger);
		totalCountMap.put(key, totalCount);
	}
	
	/**
	 * 获取增量计数器
	 * @param app
	 * @return
	 */
	public static AtomicInteger getIncreDataCounter(ESApplication app) {
		String key = app.getAppCode() + "-" + app.getIndexCode() + "-FullData";
		if (counterMap.containsKey(key)) {
			return counterMap.get(key);
		}
		return null;
	}
	
	/**
	 * 获取增量同步总数
	 * @param app
	 * @return
	 */
	public static int getIncreDataTotalCount(ESApplication app) {
		String key = app.getAppCode() + "-" + app.getIndexCode() + "-FullData";
		if (totalCountMap.containsKey(key)) {
			return totalCountMap.get(key);
		}
		return 0;
	}
}
