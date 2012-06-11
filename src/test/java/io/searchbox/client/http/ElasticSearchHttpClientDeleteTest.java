package io.searchbox.client.http;

import io.searchbox.ElasticSearchTestServer;
import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.indices.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.logging.Logger;

import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClientDeleteTest {

    private static Logger logger = Logger.getLogger(ElasticSearchHttpClientDeleteTest.class.getName());

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        ElasticSearchTestServer.start();
        client.index(new Index("twitter", "tweet", "1", "{user:\"searchboxio\"}"));
    }

    @Test
    public void deleteIndexWithValidParameters() {
        try {
            client.delete(new Index("twitter", "tweet", "1"));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithUnValidIdParameter() {
        try {
            client.delete(new Index("twitter", "tweet", null));
        } catch (Exception e) {
            fail("Failed during the delete index with unvalid id parameter. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithUnValidTypeParameter() {
        try {
            client.delete(new Index("twitter", null, "1"));
        } catch (Exception e) {
            fail("Failed during the delete index with unvalid type parameter. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithUnValidNameParameter() {
        try {
            client.delete(new Index(null, "tweet", "1"));
        } catch (Exception e) {
            fail("Failed during the delete index with unvalid name parameter. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithUnValidParameters() {
        try {
            client.delete(new Index());
        } catch (Exception e) {
            fail("Failed during the delete index with unvalid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void deleteIndexWithNullParameters() {
        try {
            client.delete(null);
        } catch (Exception e) {
            fail("Failed during the delete index with null parameter. Exception:%s" + e.getMessage());
        }
    }


}
