package io.searchbox.client.http;

import io.searchbox.client.ElasticSearchClientFactory;
import io.searchbox.indices.Index;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClientUpdateTest {
    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject();
        client.index(new Index("twitter", "tweet", "1", "{\n" +
                "    \"counter\" : 1,\n" +
                "    \"tags\" : [\"red\"]\n" +
                "}"));
    }

    @Test
    public void updateIndexWithValidParameters() {
        try {
            client.update(new Index("twitter", "tweet", "1","{\n" +
                    "    \"script\" : \"ctx._source.counter += count\",\n" +
                    "    \"params\" : {\n" +
                    "        \"count\" : 4\n" +
                    "    }\n" +
                    "}"));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

}
