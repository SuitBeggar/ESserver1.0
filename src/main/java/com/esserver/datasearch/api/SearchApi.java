package com.esserver.datasearch.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.esserver.datasearch.service.DataSearchService;
import com.esserver.datasearch.vo.SearchParams;
import com.esserver.datasync.DataSyncEngine;

@RestController
@RequestMapping("/searchData")
public class SearchApi {

	@Autowired
	private DataSearchService elasticsearchService;
	@Autowired
	private DataSyncEngine dataSyncEngine;
	/**
	 * 搜索API  入口
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/searchFromEsServer", method = RequestMethod.POST)
	public String dataSearch(@RequestBody Map<String, Object> requestMap) {
		// Map requestMap = request.getParameterMap();
		System.out.println("******************searchFromEsServer(搜索微服务)初始化*******************");
		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, List<String>>> map = (HashMap<String, HashMap<String, List<String>>>) requestMap.get("queryBuilder");
		System.out.println("ggggg:::::"+map.toString());
		String appCode = (String) requestMap.get("appCode");
		System.out.println(appCode);
		String indexCode = (String) requestMap.get("indexCode");
		System.out.println(indexCode);
		System.out.println(requestMap.get("from"));
		System.out.println(requestMap.get("size"));
		int from = Integer.parseInt((String) requestMap.get("from"));
		int size = Integer.parseInt((String) requestMap.get("size"));
		SearchParams  searchParams = new SearchParams();
		searchParams.setPageIndex(from);
		searchParams.setPageSize(size);
		String result = elasticsearchService.queryResultAnalysis(appCode, indexCode, from, size, map);
		return result;
	}
	
	
	/**
	 * 手动全量同步 用于测试
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/testFullDataSync", method = RequestMethod.POST)
	public void testFullDataSync(@RequestBody Map<String, Object> requestMap) {//向ES中全量同步
		System.out.println("******************testFullDataSync初始化*******************");
		dataSyncEngine.startupFullDataSynchronize(1L);
		try {
			Thread.sleep(100000000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 手动增量同步 用于测试
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/testIncreDataSync", method = RequestMethod.POST)
	public void testIncreDataSync() {//向ES中增量同步 
		dataSyncEngine.startupIncreDataSynchronize(1L);
		try {
			Thread.sleep(100000000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
