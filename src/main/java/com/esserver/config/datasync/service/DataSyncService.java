package com.esserver.config.datasync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.datasync.vo.ESSyncDataSource;
import com.esserver.config.datasync.vo.ESSyncPlanConfig;

@Service
public class DataSyncService {
	
	@Autowired
	private ConfigEngine configEngine;

	
	
	/**
	 * 根据appID获取动态数据源信息
	 * @param appID
	 * @return
	 */
	public ESSyncDataSource querySyncDataSourceByAppID(Long appID) {
		for (ESApplication app : configEngine.getApplicationList()) {
			if (appID == app.getId()) {
				return app.getSyncDataSource();
			}
		}
		return null;
	}
	
	/**
	 * 根据appID获取同步策略配置
	 * @param appID
	 * @return
	 */
	public ESSyncPlanConfig querySyncPlanConfigByAppID(Long appID) {
		for (ESApplication app : configEngine.getApplicationList()) {
			if (appID == app.getId()) {
				return app.getSyncPlanConfig();
			}
		}
		return null;
	}
}
