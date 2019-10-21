package com.esserver.config.datasync.vo;

import java.util.Date;

import lombok.Data;

/**
 * 数据过滤配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESDataFilterConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 过滤字段 */
	private String filterColumns;
	
	/** 字典文件路径 */
	private String dictFilePath;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
