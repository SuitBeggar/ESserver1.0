package com.esserver.config.common.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.esserver.config.ConfigEngine;
import com.esserver.config.datasync.vo.ESSyncDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态数据源工厂，生产和存放数据源
 * 
 * @author Administrator
 *
 */
@Slf4j
@Component
@Configuration
public class DynamicDataSourceFactory {
	private DynamicRoutingDataSourceResolver resolver;

	@Autowired
	private ConfigEngine configEngine;
	

	/**
	 * 方法描述：创建数据源
	 * 
	 * @author xubincheng 20180301
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @return dataSource
	 */
	public DataSource createDataSource(String driverClassName, String url, String username, String password) {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(driverClassName);
		dataSourceBuilder.url(url);
		dataSourceBuilder.username(username);
		dataSourceBuilder.password(password);
		return dataSourceBuilder.build();
	}

	/**
	 * 方法描述：创建数据源
	 * 
	 * @author xubincheng 20180301
	 * @param sycnDataSource
	 * @return
	 */
	public DataSource createDataSource(ESSyncDataSource sycnDataSource) {
		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(sycnDataSource.getDriverClassName());
		dataSourceBuilder.url(sycnDataSource.getUrl());
		dataSourceBuilder.username(sycnDataSource.getUser());
		dataSourceBuilder.password(sycnDataSource.getPassword());
		return dataSourceBuilder.build();
	}

	@DependsOn({"configEngine"})
	@Bean
	public DynamicRoutingDataSourceResolver dataSource() {
		this.resolver = new DynamicRoutingDataSourceResolver();

		Map<Object, Object> dataSources = new HashMap<Object, Object>();
		List<ESSyncDataSource> syncDataSourceList = configEngine.queryAllSyncDataSource();
		// 初始化动态数据远
		log.info("init dynamic dataSource");
		for (ESSyncDataSource syncDataSource : syncDataSourceList) {
			DataSource dataSource = this.createDataSource(syncDataSource);
			dataSources.put(syncDataSource.getAppID(), dataSource);
			log.info("generate dynamic dataSource(appID=" + syncDataSource.getAppID() + ")");
		}
		this.resolver.setTargetDataSources(dataSources);
		log.info("complete generate dynamic dataSource[" + syncDataSourceList.size() + "]");
		return this.resolver;
	}

	/**
	 * 方法描述：新增或修改数据源
	 * 
	 * @author xubincheng 20180301
	 * @param sycnDataSource
	 */
	public void addOrUpdateSyncDataSource(ESSyncDataSource syncDataSource) {
		DataSource dataSource = this.createDataSource(syncDataSource);
		Map<Object, Object> map = this.resolver.getTargetDataSources();
		map.put(syncDataSource.getAppID(), dataSource);
		this.resolver.setTargetDataSources(map);
		this.resolver.afterPropertiesSet();
	}

}
