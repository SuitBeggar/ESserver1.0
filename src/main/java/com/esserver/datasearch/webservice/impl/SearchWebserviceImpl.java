package com.esserver.datasearch.webservice.impl;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

import com.esserver.common.elasticsearch.ElasticSearchService;
import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.datasearch.service.DataSearchService;
import com.esserver.datasearch.webservice.SearchWebservice;

@WebService(targetNamespace = "http://webservice.search.com/", endpointInterface = "com.esserver.search.webservice.SearchWebservice")
public class SearchWebserviceImpl implements SearchWebservice {

	@Autowired
	private ConfigEngine configEngine;

	@Autowired
	private DataSearchService dataSearchService;

	@Autowired
	private ElasticSearchService elasticSearchService;

	@Override
	public String searchInterface(String indexName, String indexType, int from, int size,
			HashMap<String, HashMap<String, List<String>>> map) {
		String result = dataSearchService.queryResultAnalysis(indexName, indexType, from, size, map);
		return result;
	}

	@Override
	public void dataSynchronizeInterface(String appCode, String indexCode, List<HashMap<String, Object>> dataList) {
		ESApplication application = configEngine.queryApplicationByAppCodeAndIndexCode(appCode, indexCode);
		String indexPrimaryKey = application.getSyncPrimaryTableConfig().getIndexPrimaryKey();
		elasticSearchService.createIndexByBulk(application, indexPrimaryKey, dataList);
	}
}
