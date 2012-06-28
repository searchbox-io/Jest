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
import java.util.ArrayList;
import java.util.List;

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
        try {
            executeTestCase(new Index("twitter", "tweet", "1","{\"user\":\"searchboxio\"}"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void automaticIdGeneration() {
        try {
            executeTestCase(new Index("twitter", "tweet", (Object) "{\"user\":\"searchboxio\"}"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addMultipleSourceAtOnce() {
        List<Object> sources = new ArrayList<Object>();
        sources.add("{\"user\" : \"kimchy\"}");
        sources.add("{\"post_date\" : \"2009-11-15T14:12:12\"}");
        sources.add("{\"message\" : \"trying out Elastic Search\"}");
        try {
            executeTestCase(new Index("twitter", "tweet", sources));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndex() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Index( "tweet", (Object) "{\"user\":\"searchboxio\"}","1"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexWithoutId() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Index("tweet", (Object) "{\"user\":\"searchboxio\"}"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentsToDefaultIndex() {
        List<Object> sources = new ArrayList<Object>();
        sources.add("{\"user\" : \"kimchy\"}");
        sources.add("{\"post_date\" : \"2009-11-15T14:12:12\"}");
        sources.add("{\"message\" : \"trying out Elastic Search\"}");
        try {
            executeTestCase(new Index("tweet",sources));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexAndDefaultType() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        try {
            executeTestCase(new Index((Object) "{\"user\":\"searchboxio\"}","1"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void addDocumentToDefaultIndexAndTypeWithoutId() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        try {
            executeTestCase(new Index("{\"user\":\"searchboxio\"}"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void addDocumentsToDefaultIndexAndTypeWithoutId() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        List<Object> sources = new ArrayList<Object>();
        sources.add("{\"user\" : \"kimchy\"}");
        sources.add("{\"post_date\" : \"2009-11-15T14:12:12\"}");
        sources.add("{\"message\" : \"trying out Elastic Search\"}");
        try {
            executeTestCase(new Index(sources));
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
