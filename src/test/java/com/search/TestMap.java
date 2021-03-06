package com.search;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.esserver.SearchApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestMap {
	/*@Autowired
	DataSyncEngine dataSynchronizeEngine;

	@Autowired
	DataSearchService elasticsearchService;

	@Test
	public void test() {

		
		 * HashMap<String,HashMap<String,List>> map=new
		 * HashMap<String,HashMap<String,List>>();
		 * 
		 * HashMap<String,List> a = new HashMap<String,List>(); List<String>
		 * lista = new ArrayList<String>(); //
		 * lista.add("400020020172305102168"); lista.add("公司");
		 * a.put("APPLINAME",lista); a.put("POLICYNO",lista);
		 * map.put("wildcard",a);
		 

		
		 * HashMap<String,List> b = new HashMap<String,List>(); List<String>
		 * listb = new ArrayList<String>(); listb.add("*0020120177102100599*");
		 * // listb.add("*2000002*");
		 * b.put("PRPLCOMPENSATE.COMPENSATENO",listb); map.put("wildcard",b);
		 

		HashMap<String, HashMap<String, List<String>>> map = new HashMap<>();

		HashMap<String, List<String>> wildcard = new HashMap<>();
		HashMap<String, List<String>> term = new HashMap<>();

		List<String> searchWord = new ArrayList<>();
		searchWord.add("有限公司");
		List<String> platefilterWords = new ArrayList<>();
		// platefilterWords.add("1");
		List<String> riskfilterWords = new ArrayList<>();

		wildcard.put("PRPLCLAIM.CLAIMNO", searchWord);
		wildcard.put("POLICYNO", searchWord);
		wildcard.put("REGISTNO", searchWord);
		wildcard.put("INSUREDNAME", searchWord);
		wildcard.put("DAMAGENAME", searchWord);

		term.put("BUSINESSPLATE", platefilterWords);
		term.put("CLASSCODE", riskfilterWords);

		map.put("wildcard", wildcard);
		// map.put("term",term);
		// elasticsearchService.queryConditionsAnalysis(map);
		BoolQueryBuilder boolQueryBuilder = elasticsearchService.queryConditionsAnalysis(map);
		SearchResponse searchResponse = dataSynchronizeEngine.search("caseindex_index", "caseindex_index", 0, 10,
				boolQueryBuilder);
		String json = elasticsearchService.queryResultAnalysis("caseindex_index", "caseindex_index", 0, 10, map);
		log.info("结果集为：" + json);
		System.out.println("测试：" + searchResponse.getHits().getTotalHits());
	}

	public void testRrange() {
		HashMap<String, HashMap<String, List<String>>> map = new HashMap<>();

		HashMap<String, List<String>> rang = new HashMap<>();

		List<String> searchWord = new ArrayList<>();
		searchWord.add("a公司");
		// searchWord.add("11");
		rang.put("APPLINAME", searchWord);
		map.put("wildcard", rang);
		BoolQueryBuilder boolQueryBuilder = elasticsearchService.queryConditionsAnalysis(map);
		String json = elasticsearchService.queryResultAnalysis("policyindex_index", "policyindex_index", 0, 10, map);
		SearchResponse searchResponse = dataSynchronizeEngine.search("policyindex_index", "policyindex_index", 0, 10,
				boolQueryBuilder);
		logger.info("结果集为：" + json);
		System.out.println("测试：" + searchResponse.getHits().getTotalHits());

	}*/
}
