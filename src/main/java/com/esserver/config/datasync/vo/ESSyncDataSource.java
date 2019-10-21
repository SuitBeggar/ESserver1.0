package com.esserver.config.datasync.vo;

import java.util.Date;

import lombok.Data;

/**
 * 同步数据源
 * @author xubincheng 20180411
 *
 */

@Data
public class ESSyncDataSource {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 数据库类型 */
	private String databaseType;
	
	/** 数据库url */
	private String url;
	
	/** 数据库驱动名称 */
	private String driverClassName;
	
	/** 用户名 */
	private String user;
	
	/** 密码 */
	private String password;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
