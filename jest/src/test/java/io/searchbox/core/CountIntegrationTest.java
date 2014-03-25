package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
@FixMethodOrder
public class CountIntegrationTest extends AbstractIntegrationTest {

    private static final double DELTA = 1e-15;

    @Before
    public void setup() {
        createIndex("cvbank", "office_docs");
    }

    @Test
    public void countWithMultipleIndices() {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        try {
            CountResult result = client.execute(new Count.Builder()
                    .query(query)
                    .addIndex("cvbank")
                    .addIndex("office_docs")
                    .build());
            assertNotNull(result);
            assertTrue("count operation should be successful", result.isSucceeded());
            assertEquals(0.0, result.getCount(), DELTA);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void countWithValidTermQuery1() {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        try {
            CountResult result = client.execute(new Count.Builder().query(query).build());
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(0.0, result.getCount(), DELTA);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void countWithValidTermQuery2() {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        try {
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
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            assertEquals(1.0, result.getCount(), DELTA);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

}
