package com.esserver.config.common.datasource;

/**
 * 动态数据源上下文，用于存放线程级动态数据源名称
 * 
 * @author xubincheng 20180111
 *
 */

public class DynamicDataSourceContextHolder {
	private static ThreadLocal<Long> contextHolder = new ThreadLocal<>();

	/**
	 * 设置当前数据源
	 * 
	 * @param dataSourceName
	 */
	public static void setDataSource(Long dataSourceName) {
		contextHolder.set(dataSourceName);
	}

	/**
	 * 获取当前数据源名称
	 * 
	 * @return 当前数据源名称
	 */
	public static Long getDataSource() {
		return contextHolder.get();
	}

	/**
	 * 清空当前数据源
	 */
	public static void clear() {
		contextHolder.remove();
	}
}
