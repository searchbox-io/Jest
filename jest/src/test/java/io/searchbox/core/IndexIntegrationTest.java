package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class IndexIntegrationTest extends AbstractIntegrationTest {

    Map<Object, Object> source = new HashMap<Object, Object>();

    @Test
    public void indexDocumentWithValidParametersAndWithoutSettings() throws IOException {
        try {
            source.put("user", "searchbox");
            JestResult result = client.execute(new Index.Builder(source).index("twitter").type("tweet").id("1").build());
            executeTestCase(result);
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void automaticIdGeneration() {
        try {
            source.put("user", "jest");
            JestResult result = client.execute(new Index.Builder(source).index("twitter").type("tweet").build());
            executeTestCase(result);
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void indexAsynchronously() {
        try {
            source.put("user", "jest");
            Action action = new Index.Builder(source).index("twitter").type("tweet").build();

            client.executeAsync(action, new JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    executeTestCase(result);
                }

                @Override
                public void failed(Exception ex) {
                    fail("Failed during the running asynchronous call");
                }

            });

        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:" + e.getMessage());
        }

        //wait for asynchronous call
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void executeTestCase(JestResult result) {
        assertNotNull(result);
        assertTrue(result.isSucceeded());
        assertEquals(true, result.getValue("ok"));
    }
}
