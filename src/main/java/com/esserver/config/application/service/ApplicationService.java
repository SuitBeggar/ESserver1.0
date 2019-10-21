package com.esserver.config.application.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;

@Service
public class ApplicationService {

	@Autowired
	private ConfigEngine configEngine;
	
	/**
	 * 根据应用ID获取应用信息
	 * @param appID
	 * @return
	 */
	public ESApplication queryApplicationByID(Long appID) {
		return configEngine.queryApplicationByAppID(appID);
	}
}
