package io.searchbox.internal.core;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.http.HttpClient;
import io.searchbox.client.http.JestHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


//@RunWith(ElasticsearchRunner.class)
//@ElasticsearchNode
public class IndexTest {

    //@Test
    public void indexCreationWithoutId() throws IOException {
        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        Client client = new HttpClient(httpClient);

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

    //@Test
    public void indexCreationWithoutIdDefaults() throws IOException {
        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new HttpClient(httpClient);

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

    //@Test
    public void indexCreationWithId() throws IOException {
        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        Client client = new HttpClient(httpClient);

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

    //@Test
    public void indexCreationWithIdDefaults() throws IOException {
        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new HttpClient(httpClient);

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


