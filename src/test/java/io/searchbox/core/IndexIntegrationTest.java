package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.ElasticSearchTestServer;
import io.searchbox.Source;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class IndexIntegrationTest {

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
        ElasticSearchTestServer.start();
        ElasticSearchTestServer.setResponseEntity("{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}");
    }

    @After
    public void tearDown() throws Exception {
        context.close();
        ElasticSearchTestServer.setResponseEntity("");
    }

    @Test
    public void indexDocumentWithValidParametersAndWithoutSettings() throws IOException {
        Document document = new Document("twitter", "tweet", "1");
        document.setSource(new Source("{user:\"searchboxio\"}"));
        try {
            executeTestCase(document);
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void automaticIdGeneration() {
        Document document = new Document("twitter", "tweet");
        document.setSource(new Source("{user:\"searchboxio\"}"));
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
    }
}
