package com.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import com.esserver.SearchApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestApi {
	
	@Test
	public void testSelect() {
		HashMap<String, HashMap<String, List<String>>> queryBuilder = new HashMap<>();
		HashMap<String, List<String>> map = new HashMap<>();
		List<String> list = new ArrayList<String>();
//		list.add("RH8N201741040000014784");//***
		list.add("RH8N2017410");//xxx
		map.put("REGISTNO", list);
		map.put("POLICYNO", list);
//		queryBuilder.put("term", map);
		queryBuilder.put("match", map);//***
		queryBuilder.put("wildcard", map);//xxx
//		queryBuilder.put("prefix", map);
//		queryBuilder.put("range", map);
/*		lista.add("190045");
//		a.put("APPLINAME", lista);
		a.put("REGISTNO", lista);
		map.put("wildcard", a);*/

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("queryBuilder", queryBuilder);
		requestMap.put("appCode", "nxyw");
		requestMap.put("indexCode", "caseDispatch_index");
		requestMap.put("lighheight", "REGISTNO");
		requestMap.put("from", "0");
		requestMap.put("size", "10");
		RestTemplate restTemplate = new RestTemplate();
//		String url = "http://10.10.1.221:9016/searchData/searchFromEsServer";
		String url = "http://localhost:9016/searchData/searchFromEsServer";
		long startTime = System.currentTimeMillis();
		String result = restTemplate.postForObject(url, requestMap, String.class);
		log.info("耗时："+ (System.currentTimeMillis() - startTime) +"ms, 结果集为：" + result);
	}

	
	
	
	@Test
	public void testFullDataSync() {
		Map<String, Object> requestMap = new HashMap<>();
		RestTemplate restTemplate = new RestTemplate();
//		String url = "http://localhost:9016/searchData/testFullDataSync";
		String url = "http://10.10.1.221:9016/searchData/testFullDataSync";
		restTemplate.postForObject(url, requestMap, String.class);
	}
	
	
	/*public void testData() {
		String indexName = "caseindex_index";
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://192.168.20.83:9016/dataSynchronized/incrementDataSynchronized?indexName" + indexName;
		String result = restTemplate.getForObject(url, String.class, indexName);
		log.info("返回结果：" + result);
	}*/

	/*public void s() {
		HashMap<String, HashMap<String, List<String>>> map = new HashMap<>();
		HashMap<String, List<String>> a = new HashMap<>();
		List<String> lista = new ArrayList<String>();
		// lista.add("400020020172305102168");
		lista.add("公司");
		a.put("APPLINAME", lista);
		a.put("POLICYNO", lista);
		map.put("wildcard", a);

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("map", map);
		requestMap.put("indexName", "policyindex_index");
		requestMap.put("indexType", "policyindex_index");
		requestMap.put("from", "0");
		requestMap.put("size", "10");
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://192.168.20.83:9016/searchData/searchFromEsServer";
		String result = restTemplate.postForObject(url, requestMap, String.class);
		log.info("返回结果：" + result);
	}*/
	
	
	/*public void test2() {
		// 创建查询客户端
		ESClient client = ESClient.generate("nxyw", "case_index");
		
		// 构造查询条件
		QueryBuilder queryBuilder = client.getBuilder();
		
		// 查询条件api调用
		queryBuilder.addMatch("policyno", "PILU 154");
		queryBuilder.addTerm("riskCode", "ILU");
		
		List<XX> xx = client.search(queryBuilder, XX.class);
	}*/

}
