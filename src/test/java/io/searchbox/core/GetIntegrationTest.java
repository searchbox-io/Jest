package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.ElasticSearchTestServer;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.configuration.SpringClientTestConfiguration;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class GetIntegrationTest {
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
    public void getIndex() {
        Document document = new Document("twitter", "tweet", "1");
        ElasticSearchTestServer.setResponseEntity( "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}");
        try {
            ElasticSearchResult result = client.execute(new Get(document));
            assertNotNull(result);
            assertEquals("1", result.getId());
            assertEquals("tweet", result.getType());
            assertEquals("twitter", result.getIndexName());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

}
