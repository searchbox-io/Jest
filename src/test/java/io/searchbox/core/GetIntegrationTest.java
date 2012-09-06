package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class GetIntegrationTest extends AbstractIntegrationTest{


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
