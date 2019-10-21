package com.esserver.config.datasync.vo;

import java.util.Date;

import lombok.Data;

/**
 * 字段分词配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESAnalyzeConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 分词字段 */
	private String columnName;
	
	/** 分词器 */
	private String analyzeType;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
