package com.esserver.common.elasticsearch;

import static com.esserver.common.utils.CommonUtil.isValidDate;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.stereotype.Component;


/**
 * 搜索条件封装
 * @author fangyitao
 *
 */
@Component
public class ESQueryBuilderConstructor {

    /**
     * 精确查询
     * @param key
     * @param objects
     * @return
     */
    public QueryBuilder terms(String key,Object... objects){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Object object:objects){
//            if (object instanceof String||object instanceof Integer){
                if(object != null){
                    QueryBuilder queryBuilder = QueryBuilders.termQuery(key,object);
                    boolQueryBuilder.must(queryBuilder);
                }
//            }

        }
        return boolQueryBuilder;
    }
    /**
     * 通配符查询(一般用于字母和数字)
     * @param key
     * @param objects
     * @return
     */
    public QueryBuilder wildcard(String key,Object... objects){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(Object object:objects){
            if (object instanceof String||object instanceof Integer){
                if(!((String) object).isEmpty()){
                    WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key+".keyword","*"+object+"*");
                    boolQueryBuilder.should(wildcardQueryBuilder);
                }
            }
        }
        return boolQueryBuilder;
    }



    /**
     * 分词查询（一般用于中文的查询）
     * @param key
     * @param objects
     * @return
     */
    public QueryBuilder match(String key,Object... objects){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(Object object:objects){
//            if (object instanceof String||object instanceof Integer){
                if(!((String) object).isEmpty()){
                    QueryBuilder queryBuilder = QueryBuilders.matchQuery(key,object);
                    boolQueryBuilder.should(queryBuilder);
                }
//            }
        }
        return boolQueryBuilder;
    }


    /**
     * 模糊查询（全字段）
     * @param value
     * @return
     */
   public QueryBuilder queryStringAll(String value){
       if(!StringUtils.isBlank(value)){
           QueryBuilder queryBuilde = QueryBuilders.queryStringQuery(value);
           return queryBuilde;
       }
       return null;
   }

    /**
     * 模糊查询（单字段，一般用于中文）
     * @param objects
     * @return
     */
    public QueryBuilder queryString(String key,Object... objects){
        if(!StringUtils.isBlank(String.valueOf(objects))){
            QueryBuilder queryBuilder = new QueryStringQueryBuilder(String.valueOf(objects)).field(key);
            return queryBuilder;
        }
        return null;
    }
    /**
     * 前缀查询
     * @param key
     * @param objects
     * @return
     */
   public QueryBuilder prefix(String key,Object... objects){
       BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

       for(Object object:objects){
//           if (object instanceof String||object instanceof Integer){
               if(object != null){
                   QueryBuilder queryBuilder = QueryBuilders.prefixQuery(key, String.valueOf(object));
                   boolQueryBuilder.should(queryBuilder);
               }
//           }
       }
       return boolQueryBuilder;
   }

    /**
     * 区间查询
     * @param key
     * @param objects
     * @return
     */
  public QueryBuilder range(String key,Object... objects){
      BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
      for (Object object:objects){
          if(!StringUtils.isBlank(String.valueOf(object))){
              String[ ] valueString = String.valueOf(object).split(",");
              if(isValidDate(valueString[0])&&isValidDate(valueString[1])){
                  QueryBuilder queryBuilde = QueryBuilders.rangeQuery(key).format("yyyy-MM-dd HH:mm:ss").gte(valueString[0]).lte(valueString[1]);
                  boolQueryBuilder.should(queryBuilde);
              }else {
                  QueryBuilder queryBuilder = QueryBuilders.rangeQuery(key).gte(valueString[0]).lte(valueString[1]);
                  boolQueryBuilder.should(queryBuilder);
              }
          }

      }
       return boolQueryBuilder;
   }

    /**
     * 大于或等于
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder greaterEqual(String key,Object values){
        if(values != null){
            QueryBuilder queryBuilder = null;
            if(isValidDate(String.valueOf(values))){
                queryBuilder = QueryBuilders.rangeQuery(key).format("yyyy-MM-dd HH:mm:ss").gte(values);
            }else {
                queryBuilder = QueryBuilders.rangeQuery(key).gte(values);
            }
            return queryBuilder;
        }
        return null;
    }

    /**
     * 大于
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder greaterThan(String key,Object values){
        if(values != null){
            QueryBuilder queryBuilder = null;
            if(isValidDate(String.valueOf(values))){
                queryBuilder = QueryBuilders.rangeQuery(key).format("yyyy-MM-dd HH:mm:ss").gt(values);
            }else {
                queryBuilder = QueryBuilders.rangeQuery(key).gt(values);
            }
            return queryBuilder;
        }
        return null;
    }

    /**
     * 小于或等于
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder lessEqual(String key,Object values){
        if(values != null){
            QueryBuilder queryBuilder = null;
            if(isValidDate(String.valueOf(values))){
                queryBuilder = QueryBuilders.rangeQuery(key).format("yyyy-MM-dd HH:mm:ss").lte(values);
            }else {
                queryBuilder = QueryBuilders.rangeQuery(key).lte(values);
            }
            return queryBuilder;
        }
        return null;
    }

    /**
     * 小于
     * @param key
     * @param values
     * @return
     */
    public QueryBuilder lessThan(String key,Object values){
        if(values != null){
            QueryBuilder queryBuilder = null;
            if(isValidDate(String.valueOf(values))){
                queryBuilder = QueryBuilders.rangeQuery(key).format("yyyy-MM-dd HH:mm:ss").lt(values);
            }else {
                queryBuilder = QueryBuilders.rangeQuery(key).lt(values);
            }
            return queryBuilder;
        }
        return null;
    }

    public QueryBuilder match(String key,String value){
        if(!StringUtils.isBlank(value)){
            QueryBuilder queryBuilder = QueryBuilders.matchQuery(key,value);
            return queryBuilder;
        }
        return null;
    }

    public WildcardQueryBuilder wildcard(String key,String  value){
        if(!StringUtils.isBlank(value)){
            //  ElasticSearch5.5.1的String类型基本不用了，而是keyword和text。
            WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(key+".keyword","*"+value+"*");
            return wildcardQueryBuilder;
        }
        return null;
    }
}
