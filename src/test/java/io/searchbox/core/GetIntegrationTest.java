package io.searchbox.core;

import io.searchbox.ElasticSearchTestServer;
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
        try {
            executeTestCase(new Get.Builder("1").index("twitter").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getIndexWithTypeAndId() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Get.Builder("1").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getIndexWithId() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        try {
            executeTestCase(new Get.Builder("1").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    private void executeTestCase(Get get) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(get);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

}
