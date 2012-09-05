package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Action;
import io.searchbox.client.ElasticSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class ValidateIntegrationTest extends AbstractIntegrationTest{


    @Test
    public void validateQuery(){
        try {
            executeTestCase(new Validate.Builder("{query:query}").build());
        } catch (IOException e) {
            fail("Failed during the validate query with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void validateQueryWithIndex(){
        try {
            executeTestCase(new Validate.Builder("{query:query}").index("twitter").build());
        } catch (IOException e) {
            fail("Failed during the validate query with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    public void validateQueryWithIndexAndType(){
        try {
            executeTestCase( new Validate.Builder("{query:query}").index("twitter").type("tweet").build());
        } catch (IOException e) {
            fail("Failed during the validate query with valid parameters. Exception:" + e.getMessage());
        }
    }


    private void executeTestCase(Action action) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(action);
        assertNotNull(result);
        assertTrue((Boolean) result.getValue("ok"));
        assertTrue(result.isSucceeded());
    }
}
