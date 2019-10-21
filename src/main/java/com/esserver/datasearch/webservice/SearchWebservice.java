package com.esserver.datasearch.webservice;

import java.util.HashMap;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * 定义webservice搜索引擎接口
 *@author fangyitao
 *
 */
@WebService(targetNamespace = "http://webservice.search.com/")
public interface SearchWebservice {


    /**
     * 数据搜索
     * @param indexName
     * @param indexType
     * @param from
     * @param size
     * @param map
     * @return
     */
    @WebMethod
    String searchInterface(@WebParam(name = "indexName")String indexName,@WebParam(name = "indexType")String indexType,@WebParam(name = "from")int from,@WebParam(name = "size")int size,@WebParam(name = "map")HashMap<String,HashMap<String,List<String>>> map);

    /**
     * 数据同步
     * @param indexName
     * @param indexType
     * @param dataList
     * @param idColumn
     */
    @WebMethod
    void dataSynchronizeInterface(@WebParam(name = "indexName")String indexName,@WebParam(name = "indexType")String indexType,@WebParam(name = "dataList")List<HashMap<String, Object>> dataList);
}
