package io.searchbox.core;

import io.searchbox.ElasticSearchTestServer;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class CountIntegrationTest {

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
        ElasticSearchTestServer.start();
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
            ElasticSearchResult result = client.execute(new Count(query.toString()));
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
            Count count = new Count(query.toString());
            count.addIndex("cvbank");
            count.addType("candidate");
            ElasticSearchResult result = client.execute(count);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(3,result.getSourceAsObject(Integer.class));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

}
