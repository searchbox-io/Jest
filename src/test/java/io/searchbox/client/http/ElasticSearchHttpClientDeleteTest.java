package io.searchbox.client.http;

import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.indices.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClientDeleteTest {

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
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

}
