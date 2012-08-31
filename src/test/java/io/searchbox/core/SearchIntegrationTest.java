package io.searchbox.core;

import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static junit.framework.Assert.*;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author Dogukan Sonmez
 */


public class SearchIntegrationTest {


    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
       // ElasticSearchTestServer.start();
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void searchWithValidQuery() {
        QueryBuilder query = boolQuery()
                .must(termQuery("content", "test1"))
                .must(termQuery("content", "test4"))
                .mustNot(termQuery("content", "test2"))
                .should(termQuery("content", "test3"));

        try {
            ElasticSearchResult result = client.execute(new Search(query.toString()));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void searchWithValidBoolQuery() {
        QueryBuilder query = boolQuery()
                .must(termQuery("content", "Tugba"));

        try {
            Search search = new Search(query.toString());
            search.addIndex("cvbank");
            search.addType("candidate");
            ElasticSearchResult result = client.execute(search);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            List<Object> resultList = result.getSourceAsObjectList(Object.class);
            assertEquals(1,resultList);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


}
