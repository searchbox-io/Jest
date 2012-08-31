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
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class IndexIntegrationTest {

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    Map source;

    @Before
    public void setUp() throws Exception {
        source = new HashMap<Object,Object>();
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
        try {

            source.put("user","searchbox");
            executeTestCase(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void automaticIdGeneration() {
        try {
            source.put("user","jest");
            executeTestCase(new Index.Builder(source).index("twitter").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndex() {
        client.registerDefaultIndex("twitter");
        source.put("user","dogukan");
        try {
            executeTestCase(new Index.Builder(source).type("tweet").id("1").build());
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexWithoutId() {
        client.registerDefaultIndex("twitter");
        source.put("user","cool user");
        try {
            executeTestCase(new Index.Builder(source).type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexAndDefaultType() {
        //client.registerDefaultIndex("twitter");
       // client.registerDefaultType("tweet");
        source.put("user","admin");
        try {
            executeTestCase(new Index.Builder(source).id("1").build());
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexAndTypeWithoutId() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        source.put("user","sonmez");
        try {
            executeTestCase(new Index.Builder(source).build());
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    private void executeTestCase(Index index) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(index);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
