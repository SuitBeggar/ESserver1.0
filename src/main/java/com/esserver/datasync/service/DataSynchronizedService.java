package com.esserver.datasync.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esserver.common.elasticsearch.ElasticSearchService;
import com.esserver.common.utils.CommonUtil;
import com.esserver.common.utils.ThreadPoolUtil;
import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.datasync.vo.ESSyncChildTableConfig;
import com.esserver.config.datasync.vo.ESSyncPrimaryTableConfig;
import com.esserver.datasync.dao.DataSynchronizedDao;
import com.esserver.datasync.utils.CounterUtil;

import ins.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataSynchronizedService {
	@Autowired
	private DataSynchronizedDao dataSynchronizedDao;

	@Autowired
	public ElasticSearchService elasticSearchService;

	@Autowired
	private ConfigEngine configEngine;

	/**
	 * 根据同步主表配置信息查询主键集合
	 * 
	 * @param appID
	 * @return
	 */
	public List<String> queryAllPrimaryKeyListByConfig(ESSyncPrimaryTableConfig syncPTConfig) {
		return dataSynchronizedDao.queryPrimaryKey(syncPTConfig.getPrimaryKeySQL());
	}
	
	/**
	 * 根据同步主表配置信息查询增量主键集合
	 * @param application
	 * @param syncPTConfig
	 * @return
	 */
	public List<String> queryIncrePrimaryKeyListByConfig(final ESApplication application) {
		// 获取应用最后同步时间
		String lastSyncTime = application.getLastSyncTime();
		// 获取增量主键查询SQL，并替换${oldTime}
		String incrementKeySQL = application.getSyncPrimaryTableConfig().getIncrementKeySQL();
		incrementKeySQL = incrementKeySQL.replace("{oldTime}", getTimeCondByDBType(application.getSyncDataSource().getDatabaseType(), lastSyncTime));
		// 更新最后同步时间   这里还需要把LastSyncTime写到配置文件中
		application.setLastSyncTime(CommonUtil.getCurrTime());
		// 执行SQL
		return dataSynchronizedDao.queryPrimaryKey(incrementKeySQL);
	}
	
	/**
	 * 根据数据库类型获取时间条件
	 * @param dbType
	 * @param time
	 * @return
	 */
	public static String getTimeCondByDBType(String dbType, String time) {
		if ("oracle".equals(dbType)) {
			return "to_date('"+time+"', 'yyyy-mm-dd HH24:mi:ss')";
		}
		return "";	
	}

	/**
	 * 根据SQL配置信息和主键集合，开启多线程查询同步数据
	 * 
	 * @param application
	 * @param primaryKeyList
	 */
	public void multilThreadSyncSQLData(ESApplication application, List<String> primaryKeyList) {
		// 为空直接返回
		if (primaryKeyList == null || primaryKeyList.isEmpty()) {
			return;
		}

		// 获取同步策略 - 线程数
		int threadCount = application.getSyncPlanConfig().getThreadCount();

		// 初始化创建mapping映射
		try {
			elasticSearchService.createMapping(application);
		} catch (Exception e) {
			log.error("创建mapping时发生异常", e);
			throw new BusinessException("创建mapping时发生异常(appID='" + application.getId() + "')， errMsg=" + e.getMessage(),
					false);
		}

		// 分割主键集合多线程执行数据查询和生成索引数据。
		int dataSize = primaryKeyList.size();
		if (dataSize < threadCount) {
			threadCount = dataSize;
		}
		int step = dataSize / threadCount;
		for (int i = 0; i < threadCount; i++) {
			int fromIndex = i * step;
			int toIndex = (i + 1) * step;
			if (i == threadCount - 1 && toIndex < dataSize) {
				toIndex = dataSize;
			}
			// 分割主键集合
			List<String> somePrimaryKey = primaryKeyList.subList(fromIndex, toIndex);

			// 多线程并发执行数据查询和数据索引  开启一个线程
			ThreadPoolUtil.execute(generateSQLDataSyncTask(application, somePrimaryKey));
		}
	}

	/**
	 * 创建SQL数据同步任务
	 * @param application
	 * @param primaryKey
	 * @return
	 */
	public Runnable generateSQLDataSyncTask(ESApplication application, List<String> primaryKey) {
		// 主子表查询SQL配置
		ESSyncPrimaryTableConfig syncPTConfig = application.getSyncPrimaryTableConfig();
		List<ESSyncChildTableConfig> syncCTConfigList = syncPTConfig.getSyncChildTableConfigList();
		Runnable run = new Runnable() {
			@Override
			public void run() {
				// 根据应用ID切源
				configEngine.switchDataSourceByAppID(application.getId());
				
				// 开始时间
				Long startTime = System.currentTimeMillis();
				
				// 获取计数器
				AtomicInteger counter = CounterUtil.getFullDataCounter(application);
				// 获取同步总数
				int totalCount = CounterUtil.getFullDataTotalCount(application);
				
//				Map<String, Object> mainResult = null;
//				// 循环主键查询数据
//				for (String primaryKey : primaryKey) {
//					// 主表SQL
//					String primarySQL = syncPTConfig.getPrimarySQL().replace("{primarykey}", "'"+primaryKey+"'");
//					// 数据查询 - 根据主键查询唯一数据，不可查询出多条数据
//					mainResult = dataSynchronizedDao.queryUniqueData(primarySQL);
//
//					// 判断是否有返回值，且有子表SQL配置，有则查询并组装子表数据
//					if (mainResult != null) {
//						executeChildSQLQuery(application, syncCTConfigList, mainResult);
//					}
//
//					// 将数据生成对应ES索引数据   application:nxyw/case_index/2     syncPTConfig.getIndexPrimaryKey():主表的主键字段名称
//					elasticSearchService.createIndex(application, syncPTConfig.getIndexPrimaryKey(), mainResult);
//					int currCount = counter.incrementAndGet();
//					log.info("应用{appCode: '"+application.getAppCode()+"', indexCode: '"+application.getIndexCode()+"'}同步进度： ("+currCount+"/"+totalCount+")");
//					if (currCount == totalCount) {
//						log.info("应用{appCode: '"+application.getAppCode()+"', indexCode: '"+application.getIndexCode()+"'}同步结束，耗时："+(System.currentTimeMillis()-startTime)/1000+"s----------"+(System.currentTimeMillis()-startTime)+"ms");
//					}
//					mainResult.clear();
//				}
				
				List<HashMap<String, Object>> mainResultList = null;
				Map<String, Object> mainResult = null;
				// 循环主键查询数据
				for (String primaryKey : primaryKey) {
					// 主表SQL
					String primarySQL = syncPTConfig.getPrimarySQL().replace("{primarykey}", "'"+primaryKey+"'");
					// 数据查询 - 根据主键查询唯一数据，不可查询出多条数据
					mainResult = dataSynchronizedDao.queryUniqueData(primarySQL);

					// 判断是否有返回值，且有子表SQL配置，有则查询并组装子表数据
					if (mainResult != null) {
						executeChildSQLQuery(application, syncCTConfigList, mainResult);
					}

					
					mainResult.clear();
				}
				// 将数据生成对应ES索引数据   application:nxyw/case_index/2     syncPTConfig.getIndexPrimaryKey():主表的主键字段名称
				elasticSearchService.createIndex(application, syncPTConfig.getIndexPrimaryKey(), mainResult);
				int currCount = counter.incrementAndGet();
				log.info("应用{appCode: '"+application.getAppCode()+"', indexCode: '"+application.getIndexCode()+"'}同步进度： ("+currCount+"/"+totalCount+")");
				if (currCount == totalCount) {
					log.info("应用{appCode: '"+application.getAppCode()+"', indexCode: '"+application.getIndexCode()+"'}同步结束，耗时："+(System.currentTimeMillis()-startTime)/1000+"s----------"+(System.currentTimeMillis()-startTime)+"ms");
				}
				
			}
		};
		return run;
	}

	/**
	 * 执行子表数据查询及组装
	 * 
	 * @param application
	 * @param syncCTConfigList
	 * @param mainResult
	 */
	public void executeChildSQLQuery(ESApplication application, List<ESSyncChildTableConfig> syncCTConfigList,
			Map<String, Object> mainResult) {
		// 根据子表SQL配置查询子表数据进行组装
		if (syncCTConfigList != null && syncCTConfigList.size() > 0) {
			for (ESSyncChildTableConfig ctConfig : syncCTConfigList) {
				// 判断是否能匹配子表关联字段 (先转成大写)
				String fkColumn = StringUtils.upperCase(ctConfig.getFkColumn());
				// 未能找到匹配的外键关联字段，则输出异常日志，并跳过
				if (!mainResult.containsKey(fkColumn)) {
//					log.error("配置的子表SQL未能在主表字段中找到关联字段，{appID:'" + application.getId() + "', relationColumn: '"
//							+ fkColumn + "'}");
					continue;
				}
				String childSQL = ctConfig.getChildSQL();
				// 子表查询SQL拼接主表关联字段值条件
				if (childSQL.indexOf(" where ") > -1) {
					childSQL += " and " +  ctConfig.getFkRelationColumn() + "='" + (String) mainResult.get(fkColumn) + "'";
				} else {
					childSQL += " where " +  ctConfig.getFkRelationColumn() + "='" + (String) mainResult.get(fkColumn) + "'";
				}

				// 查询子表数据，并组装进主表数据对象中
				List<Map<String, Object>> childResultList = dataSynchronizedDao.queryAllData(childSQL);
				mainResult.put(ctConfig.getChildDataListName(), childResultList);
			}
		}
	}
}
