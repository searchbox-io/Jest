package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class MoreLikeThisIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void moreLikeThis() {
        try {
            executeTestCase(new MoreLikeThis.Builder("twitter", "tweet", "1", null).build());
        } catch (Exception e) {
            fail("Failed during the MoreLikeThis with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        assertFalse(result.isSucceeded());
    }

}
