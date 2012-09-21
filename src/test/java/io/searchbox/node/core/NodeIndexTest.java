package io.searchbox.node.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.client.http.NodeHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class NodeIndexTest {

    @Test
    public void indexCreationWithoutId() throws IOException {
        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        Client client = new NodeHttpClient(httpClient);

        IndexResponse response = client.prepareIndex().setIndex("articles").setType("article")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("message", "trying out JEST")
                        .endObject()
                )
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
    }

    @Test
    public void indexCreationWithoutIdDefaults() throws IOException {
        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        IndexResponse response = client.prepareIndex()
                .setSource(jsonBuilder()
                        .startObject()
                        .field("message", "trying out JEST with default index and type")
                        .endObject()
                )
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
    }

    @Test
    public void indexCreationWithId() throws IOException {
        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        Client client = new NodeHttpClient(httpClient);

        IndexResponse response = client.prepareIndex().setIndex("articles").setType("article").setId("1")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("message", "trying out JEST with id")
                        .endObject()
                )
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
        Assert.assertEquals("1", response.getId());
    }

    @Test
    public void indexCreationWithIdDefaults() throws IOException {
        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        IndexResponse response = client.prepareIndex().setId("2")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("message", "trying out JEST with id and default index and type")
                        .endObject()
                )
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals("articles", response.getIndex());
        Assert.assertEquals("article", response.type());
        Assert.assertEquals(1, response.getVersion());
        Assert.assertEquals("2", response.getId());
    }
}


