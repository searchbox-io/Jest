package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.core.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class PutMappingIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "mapping_index")
    public void testPutMapping() {
        PutMapping putMapping = new PutMapping("mapping_index", "document",
                "{ \"document\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }");
        try {
            JestResult result = client.execute(putMapping);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }
}
