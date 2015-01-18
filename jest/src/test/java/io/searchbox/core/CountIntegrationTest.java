package io.searchbox.core;

import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
@FixMethodOrder
public class CountIntegrationTest extends AbstractIntegrationTest {

    private static final double DELTA = 1e-15;

    @Before
    public void setup() {
        createIndex("cvbank", "office_docs");
    }

    @Test
    public void countWithMultipleIndices() throws IOException {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        CountResult result = client.execute(new Count.Builder()
                .query(query)
                .addIndex("cvbank")
                .addIndex("office_docs")
                .build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0.0, result.getCount(), DELTA);
    }

    @Test
    public void countWithValidTermQuery1() throws IOException {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        CountResult result = client.execute(new Count.Builder().query(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(0.0, result.getCount(), DELTA);
    }

    @Test
    public void countWithValidTermQuery2() throws IOException {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        Index index = new Index.Builder("{ \"user\":\"kimchy\" }")
                .index("cvbank")
                .type("candidate")
                .refresh(true)
                .build();
        client.execute(index);

        Count count = new Count.Builder()
                .query(query)
                .addIndex("cvbank")
                .addType("candidate")
                .build();

        CountResult result = client.execute(count);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1.0, result.getCount(), DELTA);
    }

}
