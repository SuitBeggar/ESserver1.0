package com.esserver.config.common.vo;

import java.util.Date;

import lombok.Data;
/**
 * 
 * @author xubincheng 20180411
 *
 */

@Data
public class ESUser {
	private Long id;
	
	/** 用户名 */
	private String username;
	
	/** 密码 */
	private String password;
	
	/** 昵称 */
	private String nickname;

	/** 插入时间 */
	private Date insertTimeForHis;

	/** 最后更新时间 */
	private Date operateTimeForHis;
}
