package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class DeleteTest {
    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void deleteIndexWithValidParametersAndWithoutSettings() {
        Document document = new Document("twitter", "tweet", "1");
        try {
            ElasticSearchResult result = client.execute(new Delete(document));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals("1", result.getId());
            assertEquals("tweet",result.getType());
            assertEquals("twitter",result.getIndexName());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


}
