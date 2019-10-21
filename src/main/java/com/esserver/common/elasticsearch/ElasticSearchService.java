package com.esserver.common.elasticsearch;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Service;

import com.esserver.common.utils.CommonUtil;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.datasync.vo.ESAnalyzeConfig;

import ins.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Service
public class ElasticSearchService {
	// ES客户端存放map
	private Map<Long, TransportClient> esClientMap = new HashMap<>();

	/**
	 * 创建传输客户端TransportClient:  不在集群中，通过它访问ES集群  大批量访问和查询
	 * 
	 * @return
	 */
	public TransportClient generateTransportClient(ESApplication application) {
		// 应用ID
		Long appID = application.getId();
		// 判断客户端已存在且连接节点不为空，则直接返回
		
		if (esClientMap.containsKey(appID)) {
			if (!esClientMap.get(appID).connectedNodes().isEmpty()) {
				return esClientMap.get(appID);
			}
		}

		// 创建TransportClient客户端
		Settings settings = Settings.builder().put("client.transport.ping_timeout", "10s")
				.put("cluster.name", application.getEsCluster())
				// 主动嗅探整个集群的状态，注意：当ES服务器监听使用内网服务器IP而访问使用外网IP时，不要使用client.transport.sniff为true
				.put("client.transport.ignore_cluster_name", false).put("client.transport.sniff", true).build();
		try {
			// es集群节点host集合，统一先将分割符转成逗号进行分割
			String esNodeHosts = application.getEsNodeHost();
			System.out.println("esNodeHosts::"+esNodeHosts);
			esNodeHosts = esNodeHosts.replaceAll(";", ",").replaceAll("，", ",");
			String[] arrNodeHost = esNodeHosts.split(",");
			TransportClient client = new PreBuiltTransportClient(settings);
			for (String host : arrNodeHost) {
				String ip = StringUtils.trim(host.split(":")[0]);
				String port = StringUtils.trim(host.split(":")[1]);
				TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(ip),
						Integer.parseInt(port));
				client.addTransportAddress(transportAddress);
				//传输客户端 把所有集群放到客户端中  图
			}
			esClientMap.put(appID, client);
			return client;
		} catch (java.net.UnknownHostException e) {
			log.error("创建ES的TransportClient发生异常", e);
			throw new BusinessException("创建ES的TransportClient发生异常， " + e.getMessage(), false);
		}
	}

	/**
	 * 组织es的mapping映射
	 * 
	 * @param analyzeFields
	 * @param indexType
	 * @return
	 */
	public XContentBuilder getMapping(List<ESAnalyzeConfig> analizeConfigList, String indexCode) throws Exception {
		XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject(indexCode);
		List<String> formatList = new ArrayList<>();
		formatList.add("dateOptionalTime");
		formatList.add("yyyy/MM/dd HH:mm:ss Z||yyyy/MM/dd Z");
		formatList.add("yyyy-MM-dd HH:mm:ss.SSS");
		formatList.add("yyyy-MM-dd HH:mm:ss");
		formatList.add("yyyy-MM-dd");
		formatList.add("dd-MM-yyyy");
		formatList.add("yyyy/MM/dd");
		mapping = mapping.field("dynamic_date_formats", formatList);
		mapping = mapping.startObject("properties");
		if (analizeConfigList != null && analizeConfigList.size() > 0) {
			for (ESAnalyzeConfig analyzeConfig : analizeConfigList) {
				String columnName = analyzeConfig.getColumnName();
				if (StringUtils.isNotBlank(columnName)) {
					// 没有分词器，默认standard标准分词器
					if (StringUtils.isBlank(analyzeConfig.getAnalyzeType())) {
						analyzeConfig.setAnalyzeType("standard");
					}
					// 创建mapping，查询分词器同用分词器选择
					mapping = mapping.startObject(columnName).field("type", "text").field("store", "yes")
							.field("analyzer", analyzeConfig.getAnalyzeType())
							.field("search_analyzer", analyzeConfig.getAnalyzeType()).endObject();
				}
			}
		}
		mapping = mapping.endObject().endObject().endObject();
		return mapping;
	}

	/**
	 * 创建es的mapping映射
	 * 
	 * @param mapping
	 * @param indexName
	 * @param indexType
	 * @return
	 * @throws Exception
	 */
	public PutMappingResponse createMapping(ESApplication application) throws Exception {
		// 获取mapping
		XContentBuilder mapping = this.getMapping(application.getAnalyzeConfigList(), application.getIndexCode());
		// 获取客户端
		TransportClient client = this.generateTransportClient(application);

		PutMappingResponse mappingResponse = null;
		// 判断索引是否已创建，未创建时初始化创建索引分片
		if (!this.assertIndex(application)) {
			// 默认5份主分片，每份个1份从分片
			XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("number_of_shards", 5)
					.field("number_of_replicas", 1).endObject();
			client.admin().indices().prepareCreate(application.getAppCode()).setSettings(builder).execute().actionGet();
		}
		PutMappingRequest mappingRequest = Requests.putMappingRequest(application.getAppCode())
				.type(application.getIndexCode()).source(mapping);
		mappingResponse = client.admin().indices().putMapping(mappingRequest).actionGet();
		return mappingResponse;
	}

	/**
	 * 判断索引是否存在
	 * 
	 * @param application
	 * @return
	 */
	public boolean assertIndex(ESApplication application) {
		// 获取客户端
		TransportClient client = this.generateTransportClient(application);

		// 请求判断是否存在索引
		IndicesExistsResponse response = client.admin().indices()
				.exists(new IndicesExistsRequest().indices(new String[] { application.getAppCode() })).actionGet();
		if (response != null) {
			return response.isExists();
		} else {
			log.error("查询索引是否存在异常，response响应为null，可能es服务未成功响应");
			return false;
		}
	}

	/**
	 * 索引数据
	 * 
	 * @author fangyitao
	 * @param indexName
	 * @param typeName
	 * @param data
	 * @param idName
	 */
	public void createIndex(ESApplication application, String indexPrimaryKey, Map<String, Object> data) {
		TransportClient client = this.generateTransportClient(application);
		IndexRequestBuilder requestBuilder = client.prepareIndex(application.getAppCode(), application.getIndexCode());

		JSONObject jsonObject = JSONObject.fromObject(data);
		String id = jsonObject.getString(StringUtils.upperCase(indexPrimaryKey));
		requestBuilder.setSource(data).setId(id).execute().actionGet();
	}

	/**
	 * 索引数据集合
	 * 
	 * @author：fangyitiao
	 * @param indexName
	 *            索引名称，相当于数据库名称
	 * @param typeName
	 *            索引类型，相当于数据库中的表名
	 * @param idColumn
	 *            id名称，相当于每个表中某一行记录的标识
	 */
	public void createIndexList(ESApplication application, String indexPrimaryKey, List<Map<String, Object>> dataList) {
		TransportClient client = this.generateTransportClient(application);
		IndexRequestBuilder requestBuilder = client.prepareIndex(application.getAppCode(), application.getIndexCode());
		// 设置索引名称,索引类型,id
		if (CommonUtil.isNotEmptyList(dataList)) {
			for (Map<String, Object> data : dataList) {
				JSONObject jsonObject = JSONObject.fromObject(data);
				String id = jsonObject.getString(StringUtils.upperCase(indexPrimaryKey));
				requestBuilder.setSource(data).setId(id).execute().actionGet();
			}
		}
	}

	/**
	 * 批量索引数据集合
	 * 
	 * @param indexName
	 * @param typeName
	 * @param dataList
	 * @param idColumn
	 */
	public void createIndexByBulk(ESApplication application, String indexPrimaryKey,
			List<HashMap<String, Object>> dataList) {
		TransportClient client = this.generateTransportClient(application);
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
		if (CommonUtil.isNotEmptyList(dataList)) {
			int count = 0;
			IndexRequestBuilder indexRequestBuilder = client.prepareIndex(application.getAppCode(),
					application.getIndexCode());
			for (Map<String, Object> data : dataList) {
				JSONObject jsonObject = JSONObject.fromObject(data);
				String id = jsonObject.getString(StringUtils.upperCase(indexPrimaryKey));
				bulkRequestBuilder.add(indexRequestBuilder.setSource(data).setId(id));
				// 每100条插入一次
				if (count % 100 == 0) {
					bulkRequestBuilder.execute().actionGet();
				}
				count++;
			}
			bulkRequestBuilder.execute().actionGet();
		}
	}

	/**
	 * 更新索引
	 * 
	 * @param application
	 *            应用信息
	 * @param indexPrimaryKey
	 *            索引主键字段名称
	 * @param dataList
	 *            更新数据
	 */
	@SuppressWarnings("deprecation")
	public void updateIndex(ESApplication application, String indexPrimaryKey, List<String> dataList) {
		TransportClient client = this.generateTransportClient(application);
		if (CommonUtil.isNotEmptyList(dataList)) {
			for (String data : dataList) {
				JSONObject jsonObject = JSONObject.fromObject(data);
				String id = jsonObject.getString(StringUtils.upperCase(indexPrimaryKey));
				UpdateRequest updateRequest = new UpdateRequest();
				updateRequest.index(application.getAppCode());
				updateRequest.type(application.getIndexCode());
				updateRequest.id(id);
				updateRequest.doc(data);
				client.update(updateRequest).actionGet();
			}
		}
	}

	/**
	 * 删除索引
	 * 
	 * @param application
	 *            应用信息
	 * @param id
	 *            待删除索引id
	 */
	public void deleteIndex(ESApplication application, String id) {
		generateTransportClient(application).prepareDelete(application.getAppCode(), application.getIndexCode(), id)
				.get();
	}

	/**
	 * 使用“_doc”全量查询
	 * 
	 * @author fangyitiao
	 */
	public SearchResponse searcher(ESApplication application) {
		SearchResponse searchResponse = this.createSearchRequestBuilder(application)
				.setQuery(QueryBuilders.matchAllQuery()).addSort(SortBuilders.fieldSort("_doc")).setSize(30)
				// 这个游标维持多长时间
				.setScroll(TimeValue.timeValueMinutes(8)).execute().actionGet();
		return searchResponse;
	}

	/**
	 * 全量搜索(配合“_doc”使用)
	 * 
	 * @author：fangyitiao
	 * @return
	 */
	public SearchResponse scanData(ESApplication application, String scrollId) {
		SearchResponse searchResponse = generateTransportClient(application).prepareSearchScroll(scrollId)
				.setScroll(TimeValue.timeValueMinutes(8)).execute().actionGet();
		return searchResponse;
	}

	/**
	 * 执行精确搜索
	 * 
	 * @author：fangyitiao
	 * @param indexName
	 *            索引名称
	 * @param type
	 *            索引类型
	 * @param queryBuilder
	 *            查询条件
	 * @return
	 */
	public SearchResponse search(ESApplication application, int from, int size, QueryBuilder queryBuilder) {
		HighlightBuilder hiBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
		hiBuilder.preTags("<span style=\"background:yellow\">");
		hiBuilder.postTags("</span>");
		/*
		ScoreSortBuilder scoreSortBuilders = SortBuilders.scoreSort().order(SortOrder.DESC);
		FieldSortBuilder sortBuilders = SortBuilders.fieldSort("INDATE.keyword").order(SortOrder.DESC);
		log.info(this.createSearchRequestBuilder(application).setQuery(queryBuilder).setFrom(from).setSize(size)
				.highlighter(hiBuilder).setExplain(true).addSort(scoreSortBuilders).addSort(sortBuilders).toString());
		SearchResponse serachResponse = this.createSearchRequestBuilder(application).setQuery(queryBuilder)
				.setFrom(from).setSize(size).highlighter(hiBuilder).setExplain(true).addSort(scoreSortBuilders)
				.addSort(sortBuilders).execute().actionGet();*/
		log.info(this.createSearchRequestBuilder(application).setQuery(queryBuilder).setFrom(from).setSize(size).highlighter(hiBuilder).toString());
		SearchResponse serachResponse = this.createSearchRequestBuilder(application).setQuery(queryBuilder)
				.setFrom(from).setSize(size).highlighter(hiBuilder).execute().actionGet();
		return serachResponse;
	}
	
	

	/**
	 * 执行精确搜索(分页)
	 * 
	 * @param indexName
	 *            索引名称
	 * @param type
	 *            索引类型
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页显示数目
	 * @param queryBuilder
	 *            查询条件
	 * @return
	 */
	public SearchResponse searchPage(ESApplication application, int pageNo, int pageSize, QueryBuilder queryBuilder,
			String field, SortOrder order) {
		SearchResponse serachResponse = this.createSearchRequestBuilder(application).setQuery(queryBuilder)
				.addSort(field + ".keyword", order).setFrom((pageNo - 1) * pageSize).setSize(pageSize).setExplain(true)
				.execute().actionGet();
		return serachResponse;
	}

	/**
	 * 执行模糊搜索（分页）
	 * 
	 * @param indexName
	 *            索引名称
	 * @param type
	 *            索引类型
	 * @param queryBuilder
	 *            查询条件
	 * @return
	 */
	public SearchResponse searchPage(ESApplication application, int pageNo, int pageSize, QueryBuilder queryBuilder) {
		SearchResponse serachResponse = this.createSearchRequestBuilder(application).setQuery(queryBuilder)
				.setFrom((pageNo - 1) * pageSize).setSize(pageSize).execute().actionGet();
		return serachResponse;
	}

	/**
	 * 执行模糊搜索（分页）
	 * 
	 * @param indexName
	 *            索引名称
	 * @param type
	 *            索引类型
	 * @param queryBuilder
	 *            查询条件
	 * @return
	 */
	public SearchResponse searchPage(ESApplication application, int pageNo, int pageSize,WildcardQueryBuilder queryBuilder) {
		SearchResponse serachResponse = this.createSearchRequestBuilder(application).setQuery(queryBuilder)
				.setFrom((pageNo - 1) * pageSize).setSize(pageSize).execute().actionGet();
		return serachResponse;
	}

	/**
	 * 匹配查询
	 * 
	 * @param indexName
	 * @param type
	 * @param from
	 * @param size
	 * @param queryBuilder
	 * @return
	 */
	public SearchResponse search(ESApplication application, int from, int size, WildcardQueryBuilder queryBuilder) {
		SearchResponse serachResponse = this.createSearchRequestBuilder(application).setFrom(from).setSize(size)
				.setQuery(queryBuilder).execute().actionGet();
		return serachResponse;
	}

	/**
	 * 创建查询请求对象
	 * 
	 * @param application
	 * @return
	 */
	private SearchRequestBuilder createSearchRequestBuilder(ESApplication application) {
		TransportClient client = generateTransportClient(application);
		return client.prepareSearch(application.getAppCode()).setTypes(application.getIndexCode());
	}

	/**
	 * 关闭ElasticSearch的客户端
	 * 
	 * @param client
	 */
	public static void closeClient(Client client) {
		if (client != null) {
			client.close();
			client = null;
		}
	}
}
