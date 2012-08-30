package io.searchbox.node.core;

import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.client.http.NodeHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.junit.Test;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.textQuery;

public class NodeSearchTest {

    @Test
    public void search() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        SearchResponse response = client.prepareSearch()
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(textQuery("message", "JEST"))
                .setExplain(true)
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getHits().getTotalHits());
    }

    @Test
    public void searchDefaults() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        Client client = new NodeHttpClient(httpClient);

        SearchResponse response = client.prepareSearch("articles")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(textQuery("message", "JEST"))
                .setExplain(true)
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getHits().getTotalHits());
    }
}
