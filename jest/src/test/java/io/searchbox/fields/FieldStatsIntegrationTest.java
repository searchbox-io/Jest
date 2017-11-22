package io.searchbox.fields;

import com.google.common.collect.ImmutableMap;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class FieldStatsIntegrationTest  extends AbstractIntegrationTest {

    static final String INDEX = "twitter";
    static final String TYPE = "tweet";
    static final String TEST_FIELD = "test_name";
    static final List FIELDS = Collections.singletonList(TEST_FIELD);

    @Test
    public void testFieldStats() throws IOException {

        Map<String, String> source = ImmutableMap.of(
                TEST_FIELD, "testFieldStats");

        DocumentResult documentResult = client.execute(
                new Index.Builder(source)
                        .index(INDEX)
                        .type(TYPE)
                        .refresh(true)
                        .build()
        );

        assertTrue(documentResult.getErrorMessage(), documentResult.isSucceeded());

        FieldStats fieldStats = new FieldStats.Builder(FIELDS).setIndex(INDEX).build();

        JestResult fieldStatsResult = client.execute(fieldStats);

        assertTrue(fieldStatsResult.getErrorMessage(), fieldStatsResult.isSucceeded());
    }

}
