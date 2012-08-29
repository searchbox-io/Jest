package io.searchbox.client.http;

import io.searchbox.client.ElasticSearchClientFactory;
import junit.framework.Assert;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
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

    @Test
    public void testCount() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        CountResponse response = client.prepareCount()
                .setIndices("articles")
                .setTypes("article")
                .setQuery(textQuery("user", "kimchy"))
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
    }

    @Test
    public void testGet() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        GetResponse response = client.prepareGet("articles", "article", "9nUwLmZKSNqoiJvjYkUM8A")
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
    }

    @Test
    public void testPercolate() throws IOException {

        ElasticSearchHttpClient httpClient = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new NodeHttpClient(httpClient);

        //This is the query we're registering in the percolator
        QueryBuilder qb = termQuery("content", "amazing");

        //Index the query = register it in the percolator
        IndexResponse response = client.prepareIndex("_percolator", "myIndexName", "myDesignatedQueryName")
                .setSource(qb.toString())
                .setRefresh(true) //Needed when the query shall be available immediately
                .execute().actionGet();

        Assert.assertNotNull(response);


        //Build a document to check against the percolator
        XContentBuilder docBuilder = XContentFactory.jsonBuilder().startObject();
        docBuilder.field("doc").startObject(); //This is needed to designate the document
        docBuilder.field("content", "This is amazing!");
        docBuilder.endObject(); //End of the doc field
        docBuilder.endObject(); //End of the JSON root object
        //Percolate
        PercolateResponse percolateResponse =
                client.preparePercolate("myIndexName", "myDocumentType").setSource(docBuilder).execute().actionGet();
        //Iterate over the results
        for (String result : percolateResponse) {
            //Handle the result which is the name of
            //the query in the percolator
        }

        Assert.assertNotNull(percolateResponse);
    }
}


