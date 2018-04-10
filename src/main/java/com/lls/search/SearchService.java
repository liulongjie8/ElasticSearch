package com.lls.search;
import com.lls.commons.Contants;
import com.lls.commons.SearchEnum;
import com.lls.entiry.CreditSearchResponse;
import com.lls.util.ElasticSearchUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class SearchService {

    /**
     * ES客户端
     */
    @Autowired
    private TransportClient client;


    /**
     * 通过关键字匹配企业名称、法人名称、经营范围、统一社会信用代码查询信用主体
     * @param keyWords  关键字
     * @param baseCredit 信用主体类别
     * @param pageSize  每页显示条数
     * @param pageNo   当前页面
     * @param areaId    区域ID
     * @param queryAreaType 区域类型
     * @param tags  信用标签
     * @return  信用主体集合
     */
    public CreditSearchResponse findListByKeyWords(String keyWords, String baseCredit, Integer pageSize, Integer pageNo, String areaId, String queryAreaType, String tags, String type){
        CreditSearchResponse creditResponse = new CreditSearchResponse();
        String index = getIndex(baseCredit);
        List<Map<String, Object>> resources= new ArrayList<Map<String, Object>>();
        SearchResponse response = null;
        if(StringUtils.isEmpty(keyWords)){
             response = matchAllQuery(index, index, pageSize, pageNo);
        }else{
            if(Contants.LEGAL_INDEX.equals(index)){
                response = queryAll(index,queryAreaType,pageSize,pageNo,keyWords,areaId,null, tags , SearchEnum.categoryOf(type));
            }
        }
        creditResponse = formatSearchResponse(response);
        return creditResponse;
    }
    /**
     * 根据信用类型获取索引类型
     * @param baseCredit  信用类型 L表示法人   P表示自然人
     * @return  当前信用类型的索引名称
     */
    private String getIndex(String baseCredit){
        return Contants.LEGAL.equals(baseCredit) ? Contants.LEGAL_INDEX : Contants.PERSON_INDEX;
    }

    /**
     * MatchAllQuery
     * @param index  索引
     * @param type   类型
     * @param pageSize  页大小
     * @param pageNo  页码
     * @return
     */
    private SearchResponse matchAllQuery(String index, String type, Integer pageSize, Integer pageNo){
        SearchResponse response = this.client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(pageNo)
                .setSize(pageSize).get();
        return  response;
    }

    /**
     * 通过关键字匹配企业名称、法人名称、经营范围、统一社会信用代码查询信用主体
     * 如果区域ID 为NULL， 则不过滤区域
     * 如果信用标签为NULL， 则不过滤信用标签
     * @param index  索引名称
     * @param type   索引类型
     * @param pageSize  每页显示条数
     * @param pageNo   当前页码
     * @param keyWords  关键字
     * @param areaId  区域ID
     * @param queryAreaName  查询区域字段名称
     * @param tags  信用标签
     * @return
     */
    private SearchResponse queryAll(String index, String type, Integer pageSize, Integer pageNo
    ,String keyWords, String areaId, String queryAreaName, String tags , SearchEnum searchType){
        QueryBuilder builder = null;
        switch (searchType){

            case ALL:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("name", keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("legal_person_name", keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("dz",keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("jyfw",keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("l_credit_code", keyWords).slop(100));
                break;
            case NAME:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("name", keyWords).slop(100));
                break;
            case JYFW:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("jyfw", keyWords).slop(100));
                break;
            case ADDRESS:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("dz", keyWords).slop(100));
                break;
            case CREDITCODE:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("l_credit_code", keyWords).slop(100));
                break;
            case LEGALPERSON:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("legal_person_name", keyWords).slop(100));
                break;
            default:
                builder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchPhraseQuery("name", keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("legal_person_name", keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("dz",keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("jyfw",keyWords).slop(100))
                        .should(QueryBuilders.matchPhraseQuery("l_credit_code", keyWords).slop(100));
                break;
        }
        SearchRequestBuilder searchBuild= this.client.prepareSearch(index).setTypes(index).setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(builder);
        if(!StringUtils.isEmpty(tags)){  // 添加标签过滤
            searchBuild.setPostFilter(QueryBuilders.matchPhraseQuery("characteristics_code",tags));
        }
        if(!StringUtils.isEmpty(queryAreaName)){ // 添加区域过滤
            searchBuild.setPostFilter(QueryBuilders.matchPhraseQuery( queryAreaName,areaId));
        }
        return searchBuild.get();
    }

    /**
     * CreditSearchResponse 数据转换为Map
     * @param response  SearchRespnse
     * @return
     */
    private CreditSearchResponse  formatSearchResponse(SearchResponse response){
        CreditSearchResponse creditResponse = new CreditSearchResponse();
        if(response.status()!=RestStatus.OK){
            creditResponse.setCode(RestStatus.FAILED_DEPENDENCY.toString());
            return creditResponse;
        }
        creditResponse.setCode(RestStatus.OK.toString());
        List<Map<String, Object>> resources= new ArrayList<Map<String, Object>>();
        SearchHits hits=response.getHits();
        creditResponse.setCount(hits.getTotalHits());
        for(SearchHit hit : hits){
            Map<String, Object> map =  hit.getSource();
            resources.add(map);
        }
        creditResponse.setData(resources);
        return creditResponse;
    }

}
