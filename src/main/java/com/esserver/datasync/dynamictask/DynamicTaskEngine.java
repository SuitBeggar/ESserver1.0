package com.esserver.datasync.dynamictask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.datasync.vo.ESSyncPlanConfig;

import lombok.extern.slf4j.Slf4j;


/**
 * 动态线程池任务调度引擎
 * 
 * @author xubincheng 20180111
 *
 */
@Slf4j
@Component
public class DynamicTaskEngine {
	/** 线程池任务调度 */
	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	/** 同步开关：0-关 ，1-开*/
	private static final String SWITCH_SYNC_OFF = "0";
	
	/** 全量数据同步任务集合 */
	private Map<Long, ScheduledFuture<?>> fullDataSyncTaskMap = new HashMap<>();
	
	/** 增量数据同步任务集合 */
	private Map<Long, ScheduledFuture<?>> increDataSyncTaskMap = new HashMap<>();

	@Bean
	private ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(10);
		threadPoolTaskScheduler.setThreadNamePrefix("动态定时任务线程");
		return threadPoolTaskScheduler;
	}
	
	/**
	 * 创建全量数据同步任务
	 * @param application
	 * @param runnable
	 */
	public void createFullDataSyncTask(ESApplication application, Runnable fullDataSyncTask) {
		Long appID = application.getId();
		// 获取同步策略
		ESSyncPlanConfig syncPlanConfig = application.getSyncPlanConfig();
		// 检查开关
		if (SWITCH_SYNC_OFF.equals(syncPlanConfig.getFullSyncSwitch())) {
			return;
		}
		// 停止已存在的定时任务
		if (fullDataSyncTaskMap.containsKey(appID)) {
			fullDataSyncTaskMap.get(appID).cancel(true);
		}
		// 创建定时任务
		log.info("createFullDataSyncTask(appID=" + appID + ")");
		ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(fullDataSyncTask,
				new CronTrigger(syncPlanConfig.getIncreSyncCronExp()));
		fullDataSyncTaskMap.put(appID, scheduledFuture);
	}
	
	/**
	 * 创建全量数据同步任务
	 * @param application
	 * @param runnable
	 */
	public void createIncreDataSyncTask(ESApplication application, Runnable increDataSyncTask) {
		Long appID = application.getId();
		// 获取同步策略
		ESSyncPlanConfig syncPlanConfig = application.getSyncPlanConfig();
		// 检查开关
		if (SWITCH_SYNC_OFF.equals(syncPlanConfig.getIncreSyncSwitch())) {
			return;
		}
		// 停止已存在的定时任务
		if (increDataSyncTaskMap.containsKey(appID)) {
			increDataSyncTaskMap.get(appID).cancel(true);
		}
		// 创建定时任务
		log.info("createIncreDataSyncTask(appID=" + appID + ")");
		ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(increDataSyncTask,
				new CronTrigger(syncPlanConfig.getIncreSyncCronExp()));
		//这里应该写错了  是下面全量的才对
//		ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(increDataSyncTask,
//				new CronTrigger(syncPlanConfig.getFullSyncCronExp()));
		increDataSyncTaskMap.put(appID, scheduledFuture);
	}

	/**
	 * 停止指定应用的增量数据同步任务
	 * @param indexName
	 */
	public void closeFullDataSyncTaskByAppID(Long appID) {
		if (fullDataSyncTaskMap.containsKey(appID)) {
			// 停止任务，重新开启任务
			fullDataSyncTaskMap.get(appID).cancel(true);
			fullDataSyncTaskMap.remove(appID);
		}
	}
	
	/**
	 * 停止指定应用的增量数据同步任务
	 * @param indexName
	 */
	public void closeIncreDataSyncTaskByAppID(Long appID) {
		if (increDataSyncTaskMap.containsKey(appID)) {
			// 停止任务，重新开启任务
			increDataSyncTaskMap.get(appID).cancel(true);
			increDataSyncTaskMap.remove(appID);
		}
	}
}
