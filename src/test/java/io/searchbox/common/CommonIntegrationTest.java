package io.searchbox.common;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.AbstractAction;
import io.searchbox.client.JestResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
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

        String lineSeparator = System.getProperty("line.separator");
        String expected = "{" + lineSeparator +
                "  \"ok\" : true," + lineSeparator +
                "  \"status\" : 200," + lineSeparator +
                "  \"name\" : \"elasticsearch-test-node\"," + lineSeparator +
                "  \"version\" : {" + lineSeparator +
                "    \"number\" : \"0.90.2\"," + lineSeparator +
                "    \"snapshot_build\" : false," + lineSeparator +
                "    \"lucene_version\" : \"4.3.1\"" + lineSeparator +
                "  }," + lineSeparator +
                "  \"tagline\" : \"You Know, for Search\"" + lineSeparator +
                "}";

        assertEquals(expected, result.getJsonString());
    }
}
