package io.searchbox.internal.core;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.http.HttpClient;
import io.searchbox.client.http.JestHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;

//@RunWith(ElasticsearchRunner.class)
//@ElasticsearchNode
public class GetTest {

    //@Test
    public void get() throws IOException {

        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        Client client = new HttpClient(httpClient);

        GetResponse response = client.prepareGet("articles", "article", "1")
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
    }

    //@Test
    public void getDefaults() throws IOException {

        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        Client client = new HttpClient(httpClient);

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
