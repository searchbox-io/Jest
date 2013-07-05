package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
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
public class MultiSearchIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void multiSearch() {
        try {
            Search search = new Search.Builder("{\"match_all\" : {}}").build();
            MultiSearch multiSearch = new MultiSearch.Builder(search).build();
            executeTestCase(multiSearch);
        } catch (Exception e) {
            fail("Failed during the multi search valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void singleMultiSearchWitIndex() {
        try {
            Search search = new Search.Builder("{\"match_all\" : {}}").addIndex("twitter").build();
            MultiSearch multiSearch = new MultiSearch.Builder(search).build();
            executeTestCase(multiSearch);
        } catch (Exception e) {
            fail("Failed during the multi search valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void MultiSearchWitIndex() {
        try {
            Search search = (Search) new Search.Builder("{\"match_all\" : {}}").addIndex("twitter").build();
            Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

            MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();
            executeTestCase(multiSearch);
        } catch (Exception e) {
            fail("Failed during the multi search valid parameters. Exception:" + e.getMessage());
        }
    }

    private void executeTestCase(Action action) throws RuntimeException, IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        //assertTrue((Boolean) result.getValue("ok"));
        assertTrue(result.isSucceeded());
    }
}
