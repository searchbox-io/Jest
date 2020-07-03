package io.searchbox.indices.aliases;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class GetAliasesIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "aliases_test_index";
    private static final String INDEX_NAME_2 = "aliases_test_index2";
    private static final String INDEX_NAME_3 = "aliases_test_index3";

    @Before
    public void setup() {
        createIndex(INDEX_NAME, INDEX_NAME_2, INDEX_NAME_3);
    }

    @Test
    public void testGetAliases() throws IOException {
        String alias = "myAlias000";

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = IndicesAliasesRequest.AliasActions.add().index(INDEX_NAME).alias(alias);
        indicesAliasesRequest.addAliasAction(action);
        AcknowledgedResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        GetAliases getAliases = new GetAliases.Builder().build();
        JestResult result = client.execute(getAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().getAsJsonObject(INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(INDEX_NAME_2).getAsJsonObject("aliases").entrySet().size());
    }

    @Test
    public void testGetAliasesForSpecificIndex() throws IOException {
        String alias = "myAlias000";

        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions action = IndicesAliasesRequest.AliasActions.add().index(INDEX_NAME).alias(alias);
        indicesAliasesRequest.addAliasAction(action);
        AcknowledgedResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertNotNull(indicesAliasesResponse);
        assertTrue(indicesAliasesResponse.isAcknowledged());

        GetAliases getAliases = new GetAliases.Builder().addIndex(INDEX_NAME).build();
        JestResult result = client.execute(getAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(1, result.getJsonObject().entrySet().size());
        assertEquals(1, result.getJsonObject().getAsJsonObject(INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
    }

    @Test
    public void testGetAliasesForMultipleSpecificIndices() throws IOException {
        GetAliases getAliases = new GetAliases.Builder().addIndex(INDEX_NAME).addIndex(INDEX_NAME_3).build();
        JestResult result = client.execute(getAliases);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(2, result.getJsonObject().entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(INDEX_NAME).getAsJsonObject("aliases").entrySet().size());
        assertEquals(0, result.getJsonObject().getAsJsonObject(INDEX_NAME_3).getAsJsonObject("aliases").entrySet().size());
    }

}
