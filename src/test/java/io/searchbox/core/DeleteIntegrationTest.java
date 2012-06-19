package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.ElasticSearchTestServer;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.apache.log4j.Logger;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class DeleteIntegrationTest {

    private static Logger log = Logger.getLogger(DeleteIntegrationTest.class.getName());

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
        ElasticSearchTestServer.start();
        ElasticSearchTestServer.setResponseEntity( "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}");
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
          log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


}
