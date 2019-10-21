package com.esserver.config.datasearch.vo;

import java.util.Date;

import lombok.Data;

/**
 * 查询权限配置
 * @author xubincheng 20180411
 *
 */

@Data
public class ESQueryPowerConfig {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 请求来源 */
	private String requestOrigin;
	
	/** 密令 */
	private String secretKey;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
