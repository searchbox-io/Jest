package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
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
public class MoreLikeThisIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void moreLikeThis() {
        try {
            executeTestCase(new MoreLikeThis.Builder("1").query("query").index("twitter").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the MoreLikeThis with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void moreLikeThisWithDefaultIndex() {
        try {
            executeTestCase(new MoreLikeThis.Builder("1").query("query").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the MoreLikeThis  with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void moreLikeThisWithDefaultIndexAndType() {
        try {
            executeTestCase(new MoreLikeThis.Builder("1").query("query").build());
        } catch (Exception e) {
            fail("Failed during the MoreLikeThis with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void moreLikeThisWithoutQuery() {
        try {
            executeTestCase(new MoreLikeThis.Builder("1").index("twitter").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the MoreLikeThis with valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(action);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("ok"));
        assertFalse(result.isSucceeded());
    }

}
