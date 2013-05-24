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
            MultiSearch multiSearch = new MultiSearch();
            Search search = new Search.Builder("{\"match_all\" : {}}").build();
            multiSearch.addSearch(search);
            executeTestCase(multiSearch);
        } catch (Exception e) {
            fail("Failed during the multi search valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void singleMultiSearchWitIndex() {
        try {
            MultiSearch multiSearch = new MultiSearch();
            Search search = (Search) new Search.Builder("{\"match_all\" : {}}").addIndexName("twitter").build();
            multiSearch.addSearch(search);
            executeTestCase(multiSearch);
        } catch (Exception e) {
            fail("Failed during the multi search valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void MultiSearchWitIndex() {
        try {
            MultiSearch multiSearch = new MultiSearch();
            Search search = (Search) new Search.Builder("{\"match_all\" : {}}").addIndexName("twitter").build();
            multiSearch.addSearch(search);
            Search search2 = new Search.Builder("{\"match_all\" : {}}").build();
            multiSearch.addSearch(search2);
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
