package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class RolloverIntegrationTest extends AbstractIntegrationTest {

    private final Map<String, Object> rolloverConditions = new MapBuilder<String, Object>()
            .put("max_docs", "1")
            .put("max_age", "1d")
            .immutableMap();

    @Test
    public void testRollover() throws IOException {
        String aliasSetting = "{ \"rollover-test-index\": {} }";
        CreateIndex createIndex = new CreateIndex.Builder("rollover-test-index-000001").aliases(aliasSetting).build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        Rollover rollover = new Rollover.Builder("rollover-test-index").conditions(rolloverConditions).build();

        result = client.execute(rollover);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}
