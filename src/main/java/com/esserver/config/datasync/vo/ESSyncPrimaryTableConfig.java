package com.esserver.config.datasync.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 同步主表配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESSyncPrimaryTableConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 单表标识：0-单表 、 1-多表 */
	private String simpleTableFlag;
	
	/** 主表SQL */
	private String primarySQL;
	
	/** 主表主键查询SQL */
	private String primaryKeySQL;
	
	/** 主键名称 */
	private String primaryKeyName;
	
	/** 索引主键名称 */
	private String indexPrimaryKey;
	
	/** 增量主键查询SQL */
	private String incrementKeySQL;
	
	/** 待删除主键查询SQL */
	private String delPrimaryKeySQL;
	
	/** 同步子表SQL配置集合 */
	private List<ESSyncChildTableConfig> syncChildTableConfigList;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
