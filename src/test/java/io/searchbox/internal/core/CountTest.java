package io.searchbox.internal.core;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.http.HttpClient;
import io.searchbox.client.http.JestHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.textQuery;

//@RunWith(ElasticsearchRunner.class)
//@ElasticsearchNode
public class CountTest {

    //@Test
    //@ElasticsearchIndex(indexName = "articles")
    public void count() throws IOException {

        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();

        Client client = new HttpClient(httpClient);

        CountResponse response = client.prepareCount()
                .setIndices("articles")
                .setTypes("article")
                .setQuery(textQuery("message", "JEST"))
                .execute()
                .actionGet();

        Assert.assertEquals(1, response.count());
    }

    //@Test
    //@ElasticsearchIndex(indexName = "articles")
    public void countDefaults() throws IOException {

        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new HttpClient(httpClient);

        CountResponse response = client.prepareCount()
                .setQuery(textQuery("message", "JEST"))
                .execute()
                .actionGet();

        Assert.assertEquals(1, response.count());
    }
}
