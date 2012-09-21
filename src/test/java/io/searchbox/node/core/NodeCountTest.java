package io.searchbox.node.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.client.http.NodeHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.textQuery;

//@RunWith(ElasticsearchRunner.class)
//@ElasticsearchNode
public class NodeCountTest {

    //@Test
    //@ElasticsearchIndex(indexName = "articles")
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

    //@Test
    //@ElasticsearchIndex(indexName = "articles")
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
