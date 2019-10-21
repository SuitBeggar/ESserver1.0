package com.esserver.config.common.vo;

import java.util.Date;

import lombok.Data;

/**
 * 同步任务
 * @author xubincheng 20180411
 *
 */

@Data
public class ESSyncTask {
	private Long id;
	
	/** 应用ID */
	private Long appID;
	
	/** 同步类型 */
	private String syncType;
	
	/** 同步开始时间 */
	private Date startTime;
	
	/** 同步结束时间 */
	private Date endTime;
	
	/** 同步数据总数 */
	private Long totalCount;
	
	/** 同步成功数 */
	private Long successCount;
	
	/** 同步失败数 */
	private Long failureCount;
	
	/** 状态 */
	private String status;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
