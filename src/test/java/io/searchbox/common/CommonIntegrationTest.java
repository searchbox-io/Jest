package io.searchbox.common;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.AbstractAction;
import io.searchbox.client.JestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author ferhat
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class CommonIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testVersion() throws IOException {
        JestResult result = client.execute(new AbstractAction() {
            @Override
            public String getURI() {
                return "";
            }

            @Override
            public String getRestMethodName() {
                return "GET";
            }
        });

        assertNotNull(result);

        String expected = "{\n" +
                "  \"ok\" : true,\n" +
                "  \"status\" : 200,\n" +
                "  \"name\" : \"elasticsearch-test-node\",\n" +
                "  \"version\" : {\n" +
                "    \"number\" : \"0.90.0.RC2\",\n" +
                "    \"snapshot_build\" : false\n" +
                "  },\n" +
                "  \"tagline\" : \"You Know, for Search\"\n" +
                "}";

        assertEquals(expected, result.getJsonString());
    }
}
