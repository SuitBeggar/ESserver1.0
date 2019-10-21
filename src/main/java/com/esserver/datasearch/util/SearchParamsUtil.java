package com.esserver.datasearch.util;

import com.esserver.common.elasticsearch.ESQueryBuilderConstructor;
import com.esserver.datasearch.vo.SearchParams;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by fangyitao on 2018/7/10.
 */
public class SearchParamsUtil {

    @Autowired
    private static ESQueryBuilderConstructor esQueryBuilderConstructor;

    public static BoolQueryBuilder  createWithSearchParam(SearchParams searchParam) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (SearchParams.Params params:searchParam.getRuleList()) {
            QueryBuilder queryBuilder = null;
            switch (params.getType()){
                case SearchParams.TERM:
                    queryBuilder = esQueryBuilderConstructor.terms(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.MATCH:
                    queryBuilder =  esQueryBuilderConstructor.match(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.QUERYSTRING:
                    queryBuilder =  esQueryBuilderConstructor.queryString(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.PREFIX:
                    queryBuilder =  esQueryBuilderConstructor.prefix(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.WILDCARD:
                    queryBuilder =  esQueryBuilderConstructor.wildcard(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.RANGE:
                    queryBuilder =  esQueryBuilderConstructor.range(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.GTE:
                    queryBuilder =  esQueryBuilderConstructor.greaterEqual(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.GT:
                    queryBuilder =  esQueryBuilderConstructor.greaterThan(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.LTE:
                    queryBuilder =  esQueryBuilderConstructor.lessEqual(params.getPropertyName(),params.getValues());
                    break;
                case SearchParams.LT:
                    queryBuilder =  esQueryBuilderConstructor.lessThan(params.getPropertyName(),params.getValues());
                    break;
                default:
                    throw new IllegalArgumentException("type " + params.getType() + " not supported.");
            }
            boolQueryBuilder.must(queryBuilder);
        }
        return boolQueryBuilder;
    }
}

