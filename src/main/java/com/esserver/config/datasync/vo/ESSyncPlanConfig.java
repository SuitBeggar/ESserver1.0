package com.esserver.config.datasync.vo;

import java.util.Date;

import lombok.Data;

/**
 * 同步策略配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESSyncPlanConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 全量同步定时开关 */
	private String fullSyncSwitch;
	
	/** 全量同步定时表达 */
	private String fullSyncCronExp;
	
	/** 增量同步定时开关 */
	private String increSyncSwitch;
	
	/** 增量同步定时表达 */
	private String increSyncCronExp;
	
	/** 同步线程数 */
	private int threadCount;
	
	/** 初次启动同步标识 */
	private String firstStartSyncFlag;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
