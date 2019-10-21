package com.esserver.config.common.datasource;

import java.util.Map;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Spring提供org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource来支持DataSource路由配置
 * 重写determineCurentLookupKey()方法，返回当前需要用到的数据源名称
 * 
 * @author xubincheng 20180111
 *
 */
public class DynamicRoutingDataSourceResolver extends AbstractRoutingDataSource {
	private Map<Object, Object> targetDataSources;

	/**
	 * 重写determineCurentLookupKey()方法，返回当前需要用到的数据源名称
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceContextHolder.getDataSource();
	}

	/**
	 * 设置数据源map集合
	 */
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		this.targetDataSources = targetDataSources;
		super.setTargetDataSources(targetDataSources);
	}

	/**
	 * 获取已有数据源map集合
	 * 
	 * @return
	 */
	public Map<Object, Object> getTargetDataSources() {
		return targetDataSources;
	}
}
