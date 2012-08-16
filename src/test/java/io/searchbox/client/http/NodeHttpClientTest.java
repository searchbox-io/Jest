package io.searchbox.client.http;

import io.searchbox.client.ElasticSearchClientFactory;
import junit.framework.Assert;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.textQuery;


public class NodeHttpClientTest {

    @Test
    public void testJestClient() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        IndexResponse response = client.prepareIndex().setIndex("articles").setType("article")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elastic Search")
                        .endObject()
                )
                .execute()
                .actionGet();

        Assert.assertNotNull(response);

    }

    @Test
    public void testSearch() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        SearchResponse response = client.prepareSearch()
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(textQuery("user", "kimchy"))
                .setFrom(0).setSize(60).setExplain(true)
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
    }
}
