package io.searchbox.client.http;

import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.indices.Index;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClientGetTest {
    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        client.index(new Index("twitter", "tweet", "1", "{user:\"searchboxio\"}"));
    }

    @Test
    public void getIndexWithValidParameters() {
        try {
           Object obj = client.get("twitter", "tweet", "1");
           assertNotNull(obj);
        } catch (Exception e) {
            fail("Failed during the get index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


}
