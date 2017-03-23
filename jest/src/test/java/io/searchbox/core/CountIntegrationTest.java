package io.searchbox.core;

import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
@FixMethodOrder
public class CountIntegrationTest extends AbstractIntegrationTest {

    private static final double DELTA = 1e-15;
    private static final String INDEX_1 = "cvbank";
    private static final String INDEX_2 = "office_docs";

    @Before
    public void setup() {
        createIndex(INDEX_1, INDEX_2);
    }

    @Test
    public void countWithMultipleIndices() throws IOException {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        ensureSearchable(INDEX_1, INDEX_2);
        CountResult result = client.execute(new Count.Builder()
                .query(query)
                .addIndex(INDEX_1)
                .addIndex(INDEX_2)
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0.0, result.getCount(), DELTA);
        assertEquals("0", result.getSourceAsString());
    }

    @Test
    public void countWithValidTermQueryOnAllIndices() throws IOException {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        ensureSearchable(INDEX_1, INDEX_2);
        CountResult result = client.execute(new Count.Builder().query(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0.0, result.getCount(), DELTA);
        assertEquals("0", result.getSourceAsString());
    }

    @Test
    public void countWithValidTermQueryOnSingleIndex() throws IOException {
        String type = "candidate";
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        assertTrue(index(INDEX_1, type, "aaa1", "{ \"user\":\"kimchy\" }").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(INDEX_1);

        Count count = new Count.Builder()
                .query(query)
                .addIndex(INDEX_1)
                .addType("candidate")
                .build();

        CountResult result = client.execute(count);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1.0, result.getCount(), DELTA);
        assertEquals("1", result.getSourceAsString());
    }

}
