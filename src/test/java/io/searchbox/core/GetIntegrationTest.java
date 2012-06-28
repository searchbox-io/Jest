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
            executeTestCase(new Get("twitter", "tweet", "1"));
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getIndexDoc() {
        try {
            executeTestCase(new Get(new Doc("twitter", "tweet", "1")));
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getMultipleIndex() {
        List<Doc> docList = new ArrayList<Doc>();
        docList.add(new Doc("tweet", "twitter", "1"));
        docList.add(new Doc("test", "jest", "delete"));
        docList.add(new Doc("test", "multiple", "delete"));
        try {
            executeTestCase(new Get(docList));
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getIndexWithTypeAndId() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Get("tweet", "id"));
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getMultipleIndexWithIds() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        String[] ids = {"1", "2", "3"};
        try {
            executeTestCase(new Get(ids));
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getMultipleIndexWithTypeAndIds() {
        client.registerDefaultIndex("twitter");
        String[] ids = {"1", "2", "3"};
        try {
            executeTestCase(new Get("tweet", ids));
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
