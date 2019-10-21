package com.esserver.datasync;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esserver.common.utils.ThreadPoolUtil;
import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.datasync.dynamictask.DynamicTaskEngine;
import com.esserver.datasync.service.DataSynchronizedService;
import com.esserver.datasync.utils.CounterUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 搜索引擎基本操作类
 * 
 * @author fangyitao
 *
 */
@Slf4j
@Service
public class DataSyncEngine {

	@Autowired
	private ConfigEngine configEngine;

	@Autowired
	private DataSynchronizedService dataSynchronizedService;
	
	@Autowired
	private DynamicTaskEngine dynamicTaskEngine;

	private static final String SYNCTYPE_DATABASE = "dataSource";//数据库形式

	/**
	 * （核心）启动全量数据同步
	 * @param appID
	 */
	public void startupTimingFullDataSynchronize(Long appID) {
		// 获取应用信息
		ESApplication application = configEngine.queryApplicationByAppID(appID);
		// 区分数据同步类型 同步数据
		switch (application.getDataSyncType()) {
			case SYNCTYPE_DATABASE: // 数据库
				// 创建定时任务并执行
				Runnable fullDataSyncTask = generateFullSQLDataSyncTask(application);
				dynamicTaskEngine.createFullDataSyncTask(application, fullDataSyncTask);
				break;
		}
	}
	

	/**
	 * （核心）启动增量数据同步
	 * @param appID
	 */
	public void startupTimingIncrementDataSynchronize(Long appID) {
		// 获取应用信息
		ESApplication application = configEngine.queryApplicationByAppID(appID);
		// 区分数据同步类型 同步数据
		switch (application.getDataSyncType()) {
			case SYNCTYPE_DATABASE: // 数据库
				// 创建定时任务并执行
				Runnable increDataSyncTask = generateIncreSQLDataSyncTask(application);
				dynamicTaskEngine.createIncreDataSyncTask(application, increDataSyncTask);
				break;
		}
	}
	
	/**
	 * 直接执行全量数据同步
	 * @param appID
	 */
	public void startupFullDataSynchronize(Long appID) {
		ESApplication app = configEngine.queryApplicationByAppID(appID);
		Runnable task = generateFullSQLDataSyncTask(app);
		ThreadPoolUtil.execute(task);
	}
	
	/**
	 * 创建全量SQL数据同步任务体
	 * @param application
	 * @return
	 */
	public Runnable generateFullSQLDataSyncTask(ESApplication application) {
		Long appID = application.getId();
		Runnable run = new Runnable() {
			@Override
			public void run() {
				// 切源
				configEngine.switchDataSourceByAppID(appID);
				// 查询全量主键集合
				long startTime = System.currentTimeMillis();
				List<String> primaryKeyList = dataSynchronizedService
						.queryAllPrimaryKeyListByConfig(application.getSyncPrimaryTableConfig());
				//全量同步计数器
				CounterUtil.setFullDataCounter(application, primaryKeyList.size());
				log.info("query primaryKeyList: {appID: '" + appID + "', size:" + primaryKeyList.size() + ", useTime:'"
						+ (System.currentTimeMillis() - startTime) + "ms'}");
				// 多线程查询同步数据
				if (primaryKeyList != null && primaryKeyList.size() > 0) {
					dataSynchronizedService.multilThreadSyncSQLData(application, primaryKeyList);
				}
			}
		};
		return run;
	}
	
	
	
	/**
	 * 直接执行增量数据同步
	 * @param appID
	 */
	public void startupIncreDataSynchronize(Long appID) {
		ESApplication app = configEngine.queryApplicationByAppID(appID);
		Runnable task = generateIncreSQLDataSyncTask(app);
		ThreadPoolUtil.execute(task);
	}
	
	
	/**
	 * 创建增量SQL数据同步任务体
	 * @param application
	 * @return
	 */
	public Runnable generateIncreSQLDataSyncTask(ESApplication application) {
		Long appID = application.getId();
		Runnable run = new Runnable() {
			@Override
			public void run() {
				// 切源
				configEngine.switchDataSourceByAppID(appID);
				// 查询增量主键集合
				long startTime = System.currentTimeMillis();
				List<String> primaryKeyList = dataSynchronizedService.queryIncrePrimaryKeyListByConfig(application);
				//增量同步计数器器
				CounterUtil.setIncreDataCounter(application, primaryKeyList.size());
				log.info("query increPrimaryKeyList: {appID: '" + appID + "', size:" + primaryKeyList.size() + ", useTime:'"
						+ (System.currentTimeMillis() - startTime) + "ms'}");
				// 多线程查询同步数据
				if (primaryKeyList != null && primaryKeyList.size() > 0) {
					dataSynchronizedService.multilThreadSyncSQLData(application, primaryKeyList);
				}
			}
		};
		return run;
	}
}
