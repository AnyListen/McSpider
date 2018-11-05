package com.jointsky.edps.spider.scheduler;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.util.HashMap;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-26.
 */
public class EsScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    private final String URL_PREFIX = "lk_";

    private RestHighLevelClient client;

    public EsScheduler(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        String indexName = URL_PREFIX + task.getUUID().toUpperCase();
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            if (deleteIndexResponse.isAcknowledged()) {
                StaticLog.info("索引：" + indexName + " 删除成功！");
            } else {
                StaticLog.info("索引：" + indexName + " 删除失败！");
            }
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.NOT_FOUND) {
                StaticLog.info("索引：" + indexName + " 不存在！");
            }
        } catch (Exception e) {
            StaticLog.error(e);
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        GetRequest getRequest = buildGetRequest(task.getUUID(), request);
        try {
            return client.exists(getRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return false;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        String indexName = URL_PREFIX + task.getUUID().toUpperCase();
        String id = SecureUtil.md5(request.getUrl());
        IndexRequest indexRequest = new IndexRequest(indexName, "doc", id);
        Map<String, Object> body = new HashMap<>();
        body.put("req_url", request.getUrl());
        body.put("priority_num", request.getPriority());
        body.put("req_sta", "0");
        body.put("req_bin", Base64Encoder.encode(JSON.toJSONBytes(request)));
        indexRequest.source(body);
        try {
            client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            StaticLog.error(e);
        }
    }

    private GetRequest buildGetRequest(String uuid, Request request) {
        String indexName = URL_PREFIX + uuid.toUpperCase();
        String id = SecureUtil.md5(request.getUrl());
        GetRequest getRequest = new GetRequest(indexName, "doc", id);
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return getRequest;
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        String indexName = URL_PREFIX + task.getUUID().toUpperCase();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("req_sta", "0")));
        searchSourceBuilder.size(1).fetchSource(false);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return (int) searchResponse.getHits().getTotalHits();
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return 0;
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        String indexName = URL_PREFIX + task.getUUID().toUpperCase();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.size(1).fetchSource(false);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return (int) searchResponse.getHits().getTotalHits();
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return 0;
    }

    @Override
    public Request poll(Task task) {
        String indexName = URL_PREFIX + task.getUUID().toUpperCase();
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("req_sta", "0")));
        searchSourceBuilder.size(1).sort("priority_num", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            if (hits == null || hits.length <= 0) {
                return null;
            }
            String reqBin = hits[0].getSource().get("req_bin").toString();
            return JSON.parseObject(Base64Decoder.decode(reqBin), Request.class);
        } catch (Exception e) {
            StaticLog.error(e);
        }
        return null;
    }
}
