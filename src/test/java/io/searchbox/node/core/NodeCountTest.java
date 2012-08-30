package io.searchbox.node.core;

import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.client.http.NodeHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.textQuery;

public class NodeCountTest {

    @Test
    public void count() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();

        Client client = new NodeHttpClient(httpClient);

        CountResponse response = client.prepareCount()
                .setIndices("articles")
                .setTypes("article")
                .setQuery(textQuery("message", "JEST"))
                .execute()
                .actionGet();

        Assert.assertEquals(1, response.count());
    }

    @Test
    public void countDefaults() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        CountResponse response = client.prepareCount()
                .setQuery(textQuery("message", "JEST"))
                .execute()
                .actionGet();

        Assert.assertEquals(1, response.count());
    }
}
