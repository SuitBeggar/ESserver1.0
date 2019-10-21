package com.esserver.datasearch.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fangyitao on 2018/6/11.
 */
@Data
public class SearchParams {
    /** ASC顺序 */
    public static final int ASC_ORDER = 101;
    /** ASC逆序 */
    public static final int DESC_ORDER = 102;
    /** TERM */
    public static final int TERM = 1;
    /** MATCH */
    public static final int MATCH = 2;
    /** WILDCARD */
    public static final int WILDCARD = 3;
    /** QUERYSTRING */
    public static final int QUERYSTRING = 4;
    /** PREFIX */
    public static final int PREFIX = 5;
    /** RANGE */
    public static final int RANGE = 6;
    /** GTE 大于或等于*/
    public static final int GTE = 7;
    /** GT 大于*/
    public static final int GT = 8;
    /** LTE 小于或等于*/
    public static final int LTE = 9;
    /** LT小于 */
    public static final int LT = 10;

    private String indexName;
    private String indexType;
    private int pageIndex;
    private int pageSize;
    private Map<String, List<Map<String, Object>>> searchType;
    private List<Params> ruleList = new ArrayList<Params>();
    //子查询
    private List<SearchParams> searchParamsList = new ArrayList<SearchParams>();
    private String propertyName;
    public SearchParams(String indexName, String indexType) {
        this.indexName = indexName;
        this.indexType = indexType;
    }

    private SearchParams(String propertyName) {
        this.propertyName = propertyName;
    }

    public SearchParams addSubQueryRule(String propertyName) {
        SearchParams searchParams = new SearchParams(propertyName);
        searchParamsList.add(searchParams);
        return searchParams;
    }

    public SearchParams addTerm(String name, Object value) {
        ruleList.add(new Params(TERM,name,value));
        return this;
    }

    public SearchParams addMatch(String name, Object value) {
        ruleList.add(new Params(MATCH,name,value));
        return this;
    }

    public SearchParams addWildcard(String name, Object value) {
        ruleList.add(new Params(WILDCARD,name,value));
        return this;
    }

    public SearchParams addQqueryString(String name, Object value) {
        ruleList.add(new Params(QUERYSTRING,name,value));
        return this;
    }

    public SearchParams addPrefix(String name, Object value) {
        ruleList.add(new Params(PREFIX,name,value));
        return this;
    }

    public SearchParams addRange(String name, Object value) {
        ruleList.add(new Params(RANGE,name,value));
        return this;
    }

    public SearchParams addGreaterEqual(String name, Object value) {
        ruleList.add(new Params(GTE,name,value));
        return this;
    }

    public SearchParams addGreaterThan(String name, Object value) {
        ruleList.add(new Params(GT,name,value));
        return this;
    }

    public SearchParams addLessEqual(String name, Object value) {
        ruleList.add(new Params(LTE,name,value));
        return this;
    }

    public SearchParams addLessThan(String name, Object value) {
        ruleList.add(new Params(LT,name,value));
        return this;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setSearchType(Map<String, List<Map<String, Object>>> searchType) {
        this.searchType = searchType;
    }

    public void setRuleList(List<Params> ruleList) {
        this.ruleList = ruleList;
    }

    public void setSearchParamsList(List<SearchParams> searchParamsList) {
        this.searchParamsList = searchParamsList;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Map<String, List<Map<String, Object>>> getSearchType() {
        return searchType;
    }

    public List<Params> getRuleList() {
        return ruleList;
    }

    public List<SearchParams> getSearchParamsList() {
        return searchParamsList;
    }

    public String getPropertyName() {
        return propertyName;
    }


    public class Params implements Serializable {
        private static final long serialVersionUID = 1L;
        private int type;
        private String propertyName;
        private Object[] values;

        public Params() {
        }

        public Params(int type, String propertyName, Object... objects) {
            this.type = type;
            this.propertyName = propertyName;
            this.values = objects;
        }

        public Params(String propertyName,Object... objects) {
            this.propertyName = propertyName;
            this.values = objects;
        }

        public int getType() {
            return type;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Object[] getValues() {
            return values;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }
    }
}
