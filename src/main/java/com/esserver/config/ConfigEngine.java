package com.esserver.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.esserver.common.elasticsearch.ElasticSearchService;
import com.esserver.common.utils.CommonUtil;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.common.datasource.DynamicDataSourceContextHolder;
import com.esserver.config.datasearch.vo.ESQueryColumnConfig;
import com.esserver.config.datasync.vo.ESAnalyzeConfig;
import com.esserver.config.datasync.vo.ESDataFilterConfig;
import com.esserver.config.datasync.vo.ESSyncChildTableConfig;
import com.esserver.config.datasync.vo.ESSyncDataSource;
import com.esserver.config.datasync.vo.ESSyncPlanConfig;
import com.esserver.config.datasync.vo.ESSyncPrimaryTableConfig;
import com.esserver.config.util.XStreamUtils;
import com.esserver.datasync.DataSyncEngine;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import ins.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
/**
 * 配置引擎
 * 系统初始化启动时加载配置数据
 * 提供配置文件数据的输出接口
 * @author Administrator
 *
 */ 
@Slf4j
@Component
public class ConfigEngine {
	
	private List<ESApplication> applicationList;
	@Autowired
	ElasticSearchService elasticSearchService;
	@Autowired
	DataSyncEngine dataSyncEngine;
	
	public void init() {
		// 初始化加载xml配置文件  包括所有类图
		loadConfigXML();
		
		// 配置数据合法性检查
		String checkResult = validityCheck();
		if (StringUtils.isNotBlank(checkResult)) {
			// 抛出自定义检查异常
			throw new BusinessException(checkResult, false);
		}
		
		for(ESApplication app : applicationList){
			// 初始化生成ElasticSearch Client
			elasticSearchService.generateTransportClient(app);
			
			
			// 根据配置文件，生成数据同步任务
			ESSyncPlanConfig syncPlanConfig = app.getSyncPlanConfig();
			String fullSyncSwitch = syncPlanConfig.getFullSyncSwitch();//全量同步定时开关 
			String increSyncSwitch = syncPlanConfig.getIncreSyncSwitch();//全量同步定时开关 
			if ("Y".equals(fullSyncSwitch)) {
				dataSyncEngine.startupTimingFullDataSynchronize(app.getId());
			}
			
			if ("Y".equals(increSyncSwitch)) {
				dataSyncEngine.startupTimingIncrementDataSynchronize(app.getId());
			}
			
			// 初次启动同步标识判断
			if ("Y".equals(app.getSyncPlanConfig().getFirstStartSyncFlag())
					&& app.getLastSyncTime() == null) {//LastSyncTime初次启动不配置，后来写入
				dataSyncEngine.startupFullDataSynchronize(app.getId());
			}
		}
		
	}
	
	/**
	 * 初次启动应用时判断是否需要启动数据同步的应用  初始化执行完会执行这个方法
	 */
	@PostConstruct
	public void firstStartup() {
		for (ESApplication app : applicationList) {
			// 初次启动同步标识判断
			if ("Y".equals(app.getSyncPlanConfig().getFirstStartSyncFlag())
					&& app.getLastSyncTime() == null) {//LastSyncTime初次启动不配置，后来写入
				dataSyncEngine.startupFullDataSynchronize(app.getId());
			}
		}
		
	}
	
	/**
	 * 获取应用信息集合
	 * @return
	 */
	public List<ESApplication> getApplicationList() {
		return applicationList;
	}

	
	/**
	 * 加载配置xml文件
	 * @return
	 */
	public void loadConfigXML() {
		//TODO: 通过xstream加载配置文件
		 
		try {
			//linux::  注：SpringBoot打包为jar启动时不会将jar解压，所以不能以获取文件路径的方式读取资源文件，而是要直接获取文件流
			 InputStream inputStream = XStreamUtils.XSTREAM.getClass().getResourceAsStream("/ESApplication.xml");
			 String retStr = XStreamUtils.getread(inputStream);
			 //linux::  注：SpringBoot打包为jar启动时不会将jar解压，所以不能以获取文件路径的方式读取资源文件，而是要直接获取文件流 
			 XStream xStream = new XStream(new DomDriver());
			 XStream.setupDefaultSecurity(xStream);  
			 xStream.allowTypes(new Class[]{ESApplication.class,ESSyncChildTableConfig.class,ESAnalyzeConfig.class,ESDataFilterConfig.class,ESQueryColumnConfig.class});  
			 List<ESApplication> eSApplicationList = new ArrayList<>(); 
			 xStream.alias("ESApplication", ESApplication.class);
			 xStream.alias("ESSyncChildTableConfig", ESSyncChildTableConfig.class);
			 xStream.alias("ESAnalyzeConfig", ESAnalyzeConfig.class);
			 xStream.alias("ESDataFilterConfig",ESDataFilterConfig.class);
			 xStream.alias("ESQueryColumnConfig", ESQueryColumnConfig.class);
			 eSApplicationList =  (List<ESApplication>) xStream.fromXML(retStr);
			 System.out.println(eSApplicationList);
			 applicationList = eSApplicationList;
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
	}
	
	/**
	 * 配置文件合法性校验
	 * @param applicationList
	 * @return
	 */
	public String validityCheck() {
		StringBuffer sf = new StringBuffer();
		/**
		 * TODO: 配置合法校验
		 * 1. 应用编码 + 索引编码组合是否重复
		 * 2. 集群host书写是否有误
		 * 3. 必要数据的判空
		 * 4. 主外键关联字段是否有重复，能一一对应
		 * 5. 
		 */
		return sf.toString();
	}
	
	/**
	 * 根据应用ID获取应用信息
	 * @param appID
	 * @return
	 */
	public ESApplication queryApplicationByAppID(Long appID) {
		for (ESApplication app : applicationList) {
			if (appID!=null && appID.compareTo(app.getId())==0) {
				return app;
			}
		}
		return null;
	}
	
	/**
	 * 根据appCode和indexCode获取应用信息
	 * @param appCode
	 * @param indexCode
	 * @return
	 */
	public ESApplication queryApplicationByAppCodeAndIndexCode(String appCode, String indexCode) {
		for (ESApplication app : applicationList) {
			if (appCode.equals(app.getAppCode()) && indexCode.equals(app.getIndexCode())) {
				return app;
			}
		}
		return null;
	}
	
	/**
	 * 根据应用ID获取同步策略
	 * @param appID
	 * @return
	 */
	public ESSyncPlanConfig querySyncPlanConfigByAppID(Long appID) {
		ESApplication app = this.queryApplicationByAppID(appID);
		return app.getSyncPlanConfig();
	}
	
	/**
	 * 根据应用ID获取同步主表SQL配置
	 * @param appID
	 * @return
	 */
	public ESSyncPrimaryTableConfig querySyncPrimaryTableConfigByAppID(Long appID) {
		ESApplication app = this.queryApplicationByAppID(appID);
		return app.getSyncPrimaryTableConfig();
	}
	
	/**
	 * 获取应用中高亮字段配置集合
	 * @param application
	 * @return
	 */
	public List<String> queryHighlightColumn(ESApplication application) {
		List<String> highlightColumnList = new ArrayList<>();
		List<ESQueryColumnConfig> queryColumnConfigList = application.getQueryColumnConfigList();
		if (CommonUtil.isNotEmptyList(queryColumnConfigList)) {
			for (ESQueryColumnConfig queryColumnConfig : queryColumnConfigList) {
				// 判断是否高亮
				if ("1".equals(queryColumnConfig.getHighlightFlag())) {
					highlightColumnList.add(queryColumnConfig.getQueryColumn());
				}
			}
		}
		return highlightColumnList;
	}
	
	/**
	 * 获取所有动态数据源
	 * @return
	 */
	public List<ESSyncDataSource> queryAllSyncDataSource() {
		List<ESSyncDataSource> syncDataSourceList = new ArrayList<>();
		init();
		for (ESApplication app : applicationList) {
			syncDataSourceList.add(app.getSyncDataSource());
		}
		return syncDataSourceList;
	}

	/**
	 * 切换指定appID对应的数据源
	 * @param dataSourceName
	 */
	public void switchDataSourceByAppID(Long appID) {
		DynamicDataSourceContextHolder.setDataSource(appID);
	}

	/**
	 * 返回当前数据源对应用的应用ID
	 * @return
	 */
	public Long getCurrDataSource() {
		return DynamicDataSourceContextHolder.getDataSource();
	}
	
	
}
