package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Parameters;
import io.searchbox.client.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class GetIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void before() throws Exception {
        Index index = new Index.Builder("{\"user\":\"tweety\"}").index("twitter").type("tweet").id("1").build();
        index.addParameter(Parameters.REFRESH, true);
        client.execute(index);
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void getIndex() {
        try {

            executeTestCase(new Get.Builder("1").index("twitter").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void getIndexWithTypeAndId() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Get.Builder("1").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
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
        SearchResult result = client.execute(get);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

}
