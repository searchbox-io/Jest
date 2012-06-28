package io.searchbox.core;

import io.searchbox.ElasticSearchTestServer;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.apache.log4j.Logger;
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
        try {
            executeTestCase(new Delete("twitter", "tweet", "1"));
          log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithoutId() {
        try {
            executeTestCase(new Delete("twitter", "tweet"));
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndex() {
        try {
            executeTestCase(new Delete("twitter"));
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithDocParameter() {
        try {
            executeTestCase(new Delete(new Doc("twitter","tweet","1")));
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteMultipleIndex() {
        List<Doc> docList = new ArrayList<Doc>();
        docList.add(new Doc("tweet","twitter","1"));
        docList.add(new Doc("test","jest","delete"));
        docList.add(new Doc("test","multiple","delete"));
        try {
            executeTestCase(new Delete(docList));
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteMultipleDocumentsWithIds(){
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        String[] ids = {"1","2","3"};
        try {
            executeTestCase(new Delete(ids));
            log.info("Successfully finished document delete operation");
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    private void executeTestCase(Delete delete) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(delete);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }


}
