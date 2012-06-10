package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class IndexDocumentTest {

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
    public void indexDocumentWithValidParametersAndWithoutSettings() {
        Document document = new Document("twitter", "tweet", "1");
        document.setSource("{user:\"searchboxio\"}");
        try {
            executeTestCase(document);
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void automaticIdGeneration() {
        Document document = new Document("twitter", "tweet", null);
        document.setSource("{user:\"searchboxio\"}");
        try {
            executeTestCase(document);
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    private void executeTestCase(Document document) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(new Index(document));
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals("tweet", result.getType());
        assertEquals("twitter", result.getIndexName());
    }
}
