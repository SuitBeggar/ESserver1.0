package com.esserver.config.application.vo;

import java.util.Date;
import java.util.List;

import com.esserver.config.datasearch.vo.ESQueryColumnConfig;
import com.esserver.config.datasearch.vo.ESQueryPowerConfig;
import com.esserver.config.datasync.vo.ESAnalyzeConfig;
import com.esserver.config.datasync.vo.ESDataFilterConfig;
import com.esserver.config.datasync.vo.ESSyncDataSource;
import com.esserver.config.datasync.vo.ESSyncPlanConfig;
import com.esserver.config.datasync.vo.ESSyncPrimaryTableConfig;

import lombok.Data;

/**
 * 应用
 * @author xubincheng 20180411
 *
 */

@Data
public class ESApplication {
	private Long id;
	/** 所属用户ID */
	private Long userID;
	
	/** 应用名称 */
	private String appName;

	/** 应用编码 */
	private String appCode;

	/** 索引名称 */
	private String indexName;

	/** 索引编码 */
	private String indexCode;

	/** elasticSearch集群名称 */
	private String esCluster;

	/** elasticSearch集群节点集合 */
	private String esNodeHost;

	/** 数据同步方式 */
	private String dataSyncType;
	
	/** 最后同步时间 */
	private String lastSyncTime;

	/** 状态 */
	private String status;
	
	/** 同步数据源 */
	private ESSyncDataSource syncDataSource;
	
	/** 同步主表SQL配置 */
	private ESSyncPrimaryTableConfig syncPrimaryTableConfig;
	
	/** 分词字段配置集合 */
	private List<ESAnalyzeConfig> analyzeConfigList;
	
	/** 数据过滤配置集合 */
	private List<ESDataFilterConfig> dataFilterConfigList;
	
	/** 同步策略配置 */
	private ESSyncPlanConfig syncPlanConfig;
	
	/** 查询字段配置集合 */
	private List<ESQueryColumnConfig> queryColumnConfigList;
	
	/** 查询权限配置 */
	private ESQueryPowerConfig queryPowerConfig;
	
	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
	
}
