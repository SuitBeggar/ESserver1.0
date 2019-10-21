package com.esserver.config.datasync.vo;

import java.util.Date;

import lombok.Data;

/**
 * 同步子表配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESSyncChildTableConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 同步主表配置ID */
	private Long ptConfigID;
	
	/** 关联字段 */
	private String fkColumn;
	
	/** 关联字段 */
	private String fkRelationColumn;
	
	/** 关联字段 */
	private String childDataListName;
	
	/** 子表查询SQL */
	private String childSQL;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
