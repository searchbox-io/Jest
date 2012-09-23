package io.searchbox.internal.core;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.http.HttpClient;
import io.searchbox.client.http.JestHttpClient;
import junit.framework.Assert;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.textQuery;

//@RunWith(ElasticsearchRunner.class)
//@ElasticsearchNode
public class SearchTest {

    //@Test
    //@ElasticsearchIndex(indexName = "articles")
    public void search() throws IOException {

        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        httpClient.registerDefaultIndex("articles");
        httpClient.registerDefaultType("article");

        Client client = new HttpClient(httpClient);

        SearchResponse response = client.prepareSearch()
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(textQuery("message", "JEST"))
                .setExplain(true)
                .execute()
                .actionGet();

        Assert.assertNotNull(response);
        Assert.assertEquals(1, response.getHits().getTotalHits());
    }

    //@Test
    //@ElasticsearchIndex(indexName = "articles")
    public void searchDefaults() throws IOException {

        JestHttpClient httpClient = (JestHttpClient) new JestClientFactory().getObject();
        Client client = new HttpClient(httpClient);

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
