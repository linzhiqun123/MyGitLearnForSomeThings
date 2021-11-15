package com.clpm.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.clpm.quartz.job.JsonsRootBean;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
class QuartzApplicationTests {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private String index="bank";
    private String type="account";

    //添加索引
    @Test
    void contextLoads() {
        // 创建索引 - 请求对象
        CreateIndexRequest request = new CreateIndexRequest("quartz");
        // 发送请求，获取响应
        CreateIndexResponse response = null;
        try {
            response = restHighLevelClient.indices().create(request,
                    RequestOptions.DEFAULT);
        } catch (IOException e) {
           log.error("添加索引失败{}",e.getMessage());
        }
        boolean acknowledged = response.isAcknowledged();
       // 响应状态
      log.info("新增索引是否完成{}",acknowledged);
    }

    //查询索引
    @Test
    public void searchForIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("student");
        GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        Map<String, MappingMetaData> responseMappings = response.getMappings();

        Iterator<Map.Entry<String, MappingMetaData>> iterator = responseMappings.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String, MappingMetaData> stringMappingMetaDataEntry = iterator.next();
            log.info("key为{}",stringMappingMetaDataEntry.getKey());
            log.info("Value为{}",stringMappingMetaDataEntry.getValue());
        }

        log.info(response.toString());
    }


    //在索引下添加文档
    @Test
    public void PostIndexUser() throws IOException {
        String substring = UUID.randomUUID().toString().substring(0, 5);

        GetRequest getRequest = new GetRequest("bank", "account" , substring);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);

        if(!exists){
            //原来的不存在则创建新的文档
            JsonsRootBean jsonsRootBean = new JsonsRootBean();
            jsonsRootBean.setEmployer("2099362264@qq.com");
            jsonsRootBean.setAccountNumber(1008611);
            jsonsRootBean.setAddress("漳州市龙海市紫泥镇");
            jsonsRootBean.setAge(27);
            jsonsRootBean.setBalance(8000);
            jsonsRootBean.setEmployer("doc.Li");
            jsonsRootBean.setCity("漳州市");
            jsonsRootBean.setFirstname("林志群");
            jsonsRootBean.setGender("man");
            jsonsRootBean.setState("TW");
            jsonsRootBean.setLastname("Blue");

            IndexRequest indexRequest = new IndexRequest("bank");
            indexRequest.source(JSON.toJSONString(jsonsRootBean), XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

            //3.      打印结果信息
            System.out.println("_index:" + response.getIndex());
            System.out.println("_id:" + response.getId());
            System.out.println("_result:" + response.getResult());
        }

        log.info("UUID的值为{}",substring);
    }


    //修改文档操作
    @Test
    public void updateDocument() throws IOException {
        GetRequest getRequest = new GetRequest("bank","account","123");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("get: " + JSON.toJSONString(getResponse));
        Map<String, Object> getResponseSource = getResponse.getSource();
        JsonsRootBean jsonsRootBean = JSONObject.parseObject(JSON.toJSONString(getResponseSource), JsonsRootBean.class);
        log.info("转化后的jsonsRootBean{}",jsonsRootBean);

        if(jsonsRootBean!=null){
            //不为空则对他进行改造，并保存到elasticSearch的索引中
            jsonsRootBean.setFirstname("君奉天");
            UpdateRequest request = new UpdateRequest(index, type, "123");
            request.doc(JSON.toJSONString(jsonsRootBean), XContentType.JSON);
            UpdateResponse updateResponse = restHighLevelClient.update(request, RequestOptions.DEFAULT);

            log.info("修改是否成功{}",updateResponse);
        }
    }



    //复杂查询 搜索address中包含mill的所有人的年龄分布以及平均年龄，
//    GET bank/_search
//    {
//        "query": {
//        "match": {
//            "address": "Mill"
//        }
//    },
//        "aggs": {
//        "ageAgg": {
//            "terms": {
//                "field": "age",
//                        "size": 10
//            }
//        },
//        "ageAvg": {
//            "avg": {
//                "field": "age"
//            }
//        },
//        "balanceAvg": {
//            "avg": {
//                "field": "balance"
//            }
//        }
//    }
//    }

    @Test
    public void SearchAddressMill() throws IOException {

        //索引
        SearchRequest request = new SearchRequest();
        request.indices("bank");

        //构造搜索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchQuery("address","Mill"));


        //构建聚合分析
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("aggTerms").field("age").size(10);
        builder.aggregation(termsAggregationBuilder);

        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("ageAvg").field("age");
        builder.aggregation(avgAggregationBuilder);

        AvgAggregationBuilder aggregationBuilder = AggregationBuilders.avg("balanceAvg").field("balance");
        builder.aggregation(aggregationBuilder);

        request.source(builder);
        log.info("构造条件为{}",request);
        //2. 执行检索
        SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        log.info("搜索的结果为{}",searchResponse);

        SearchHits searchResponseHits = searchResponse.getHits();
        List jsonBeanList=new ArrayList<JsonsRootBean>();

        for (SearchHit searchResponseHit : searchResponseHits) {
            String sourceAsString = searchResponseHit.getSourceAsString();
            log.info("解析后的json串为{}",sourceAsString);
            //转为javaBean
            jsonBeanList.add(JSON.parseObject(sourceAsString, JsonsRootBean.class));
        }

        System.out.println(jsonBeanList);

        //聚合分析
        Aggregations aggregations = searchResponse.getAggregations();
        log.info("聚合分析{}",aggregations);
        Terms termsAggregation = aggregations.get("aggTerms");
        for (Terms.Bucket bucket : termsAggregation.getBuckets()) {
            Object bucketKey = bucket.getKey();
            long docCount = bucket.getDocCount();
            log.info("聚合分析结果为,年龄为{},人数有{}个",bucketKey.toString(),docCount);
        }

        Avg ageAvg = aggregations.get("ageAvg");
        log.info("聚合分析后的年龄平均值为{}",ageAvg.getValue());

        Avg balanceAvg = aggregations.get("balanceAvg");
         log.info("聚合分析后的平均薪资为{}",balanceAvg.getValue());

    }


//    GET bank/_search
//    {
//        "query": {
//        "match_all": {
//
//        }
//    },
//        "aggs": {
//        "age_terms": {
//            "terms": {
//                "field": "age",
//                        "size": 10
//            },
//            "aggs": {
//                "gender_aggs": {
//                    "terms": {
//                        "field": "gender.keyword",
//                                "size": 10
//                    },
//                    "aggs": {
//                        "balance_aggs": {
//                            "avg": {
//                                "field": "balance"
//                            }
//                        }
//                    }
//                },
//                "balance_aggs":{
//                    "avg": {
//                        "field": "balance"
//                    }
//                }
//            }
//        }
//    },
//        "size": 0
//    }
    @Test
    public void SearchForAgeAggregation() throws IOException {

        //索引
        SearchRequest request = new SearchRequest();
        request.indices("bank");

        //构造搜索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());

        //聚合条件
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("AgeTerms").field("age");
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("genderTerms").field("gender.keyword");
        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("BalanceAvg").field("balance");
        AvgAggregationBuilder builder1 = AggregationBuilders.avg("Avg").field("balance");
        termsAggregationBuilder.subAggregation(avgAggregationBuilder);
        aggregationBuilder.subAggregation(termsAggregationBuilder);
        aggregationBuilder.subAggregation(builder1);
        builder.aggregation(aggregationBuilder);
        request.source(builder);


        SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);

       log.info("searchResponse的结果集为{}",searchResponse);


        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageTerms = aggregations.get("AgeTerms");

        for (Terms.Bucket bucket : ageTerms.getBuckets()) {
            Aggregations bucketAggregations = bucket.getAggregations();
            Avg avg = bucketAggregations.get("Avg");
            log.info("年龄为{}，人数为{},平均工资为{}",bucket.getKey().toString(),bucket.getDocCount(),avg.getValue());
            Terms genderTerms = bucketAggregations.get("genderTerms");
            List<? extends Terms.Bucket> genderTermsBuckets = genderTerms.getBuckets();
            for (Terms.Bucket genderTermsBucket : genderTermsBuckets) {
                Aggregations genderTermsBucketAggregations = genderTermsBucket.getAggregations();
                Avg balanceAvg = genderTermsBucketAggregations.get("BalanceAvg");
                log.info("性别为{},人数为{},平均薪资为{}",genderTermsBucket.getKey(),genderTermsBucket.getDocCount(),balanceAvg.getValue());
            }
        }
    }


//    GET bank/_search
//    {
//        "query": {
//        "bool": {
//            "must": [
//            {
//                "match": {
//                "address": "Mill"
//            }
//            }
//      ],
//            "filter": {
//                "range": {
//                    "age": {
//                        "gte": 30,
//                                "lte": 40
//                    }
//                }
//
//            }
//        }
//    }
//    }

    @Test
    public void SearchForComplex() throws IOException {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchQuery("address","Mill"));
        boolQueryBuilder.must(QueryBuilders.matchQuery("employer","Comverges"));
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age");
        rangeQueryBuilder.lte("40");
        rangeQueryBuilder.gte("30");
        boolQueryBuilder.filter(rangeQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits searchResponseHits = searchResponse.getHits();
        for (SearchHit searchResponseHit : searchResponseHits) {
            String sourceAsString = searchResponseHit.getSourceAsString();
            log.info("解析后的json串为{}",sourceAsString);
        }
    }


    //分页和高光渲染
    @Test
    public void  SearchForHighlightAndPaging() throws IOException {
        //分页和高亮
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询全部
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("mill","address","employer"));
         searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        //分页
        searchSourceBuilder.size(10);
        searchSourceBuilder.from(0);
        //对特定字段高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("address");
        highlightBuilder.preTags("<b style='color:red'>");
        highlightBuilder.postTags("</b>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //设置request
        searchRequest.source(searchSourceBuilder);
        //查询操作
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits searchResponseHits = searchResponse.getHits();
        log.info("searchResponseHits的长度为{},集合的第一个元素为{}",searchResponseHits.getHits().length,searchResponseHits.getHits()[0].getSourceAsString());

        for (SearchHit searchResponseHit : searchResponseHits) {
            log.info("searchResponseHit的具体的值为{}",searchResponseHit.getSourceAsString());
            Map<String, HighlightField> highlightFields = searchResponseHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("address");
            System.out.println(highlightField.getFragments()[0].toString());
        }
    }


   //批量上架
    @Test
    public void BulkUpBankAccount() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(3);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchResponseHits = searchResponse.getHits();

        List<JsonsRootBean> jsonsRootBeans = new ArrayList<>();
        for (SearchHit searchResponseHit : searchResponseHits) {
            JsonsRootBean jsonsRootBean = JSON.parseObject(searchResponseHit.getSourceAsString(), JsonsRootBean.class);
            jsonsRootBeans.add(jsonsRootBean);
        }

        log.info("搜索出来的模板jsonBean为{}",jsonsRootBeans);

        //上架
        BulkRequest bulkRequest = new BulkRequest();
        jsonsRootBeans.forEach(item->{
            item.setFirstname("Lzq");
            IndexRequest indexRequest = new IndexRequest("bank");
            indexRequest.source(JSON.toJSONString(item), XContentType.JSON);
            bulkRequest.add(indexRequest);
        });

        BulkResponse bulkItemResponses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        log.info("批量上架后的结果为{}",bulkItemResponses);
    }

















}
