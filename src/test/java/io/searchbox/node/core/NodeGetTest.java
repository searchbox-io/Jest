package io.searchbox.node.core;

import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.client.http.NodeHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;

import java.io.IOException;

public class NodeGetTest {

    @Test
    public void get() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        Client client = new NodeHttpClient(httpClient);

        GetResponse response = client.prepareGet("articles", "article", "1")
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
    }

    @Test
    public void getDefaults() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        Client client = new NodeHttpClient(httpClient);
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        GetResponse response = client.prepareGet()
                .setId("1")
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
    }
}
