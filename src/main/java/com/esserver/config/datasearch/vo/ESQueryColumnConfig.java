package com.esserver.config.datasearch.vo;

import java.util.Date;

import lombok.Data;

/**
 * 字段查询配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESQueryColumnConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 查询字段 */
	private String queryColumn;
	
	/** 匹配类型 */
	private String matchType;
	
	/** 是否作为筛选条件标识 */
	private String filterCondFlag;
	
	/** 高亮标识 */
	private String highlightFlag;
	
	/** 排序标识 */
	private String sortFlag;
	
	/** 排序优先级 */
	private String sortPriority;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
