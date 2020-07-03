package io.searchbox.indices.aliases;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class AliasExistsIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_NAME_1 = "aliases_test_index1";
    private static final String INDEX_NAME_2 = "aliases_test_index2";

    @Before
    public void setup() {
        createIndex(INDEX_NAME_1, INDEX_NAME_2);
    }

    @Test
    public void testAliasesExists() throws IOException {
        String alias = "myAlias000";

        createAlias(INDEX_NAME_1, alias);

        AliasExists aliasExists = new AliasExists.Builder().build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasDoesNotExist() throws IOException {
        AliasExists aliasExists = new AliasExists.Builder().alias("does_not_exist").build();
        JestResult result = client.execute(aliasExists);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testIndexDoesNotExist() throws IOException {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("does_not_exist").build();
        JestResult result = client.execute(aliasExists);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasAndIndexDoesNotExist() throws IOException {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("does_not_exist").alias("abc").build();
        JestResult result = client.execute(aliasExists);
        assertFalse(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForSpecificIndex() throws IOException {
        String alias = "myAlias000";

        createAlias(INDEX_NAME_1, alias);

        AliasExists aliasExists = new AliasExists.Builder().addIndex(INDEX_NAME_1).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForMultipleIndices() throws IOException {
        String alias = "myAlias000";

        createAlias(INDEX_NAME_1, alias);

        AliasExists aliasExists = new AliasExists.Builder().addIndex(INDEX_NAME_1).addIndex(INDEX_NAME_2).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForSpecificAlias() throws IOException {
        String alias = "myAlias000";

        createAlias(INDEX_NAME_1, alias);

        AliasExists aliasExists = new AliasExists.Builder().alias(alias).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testAliasesExistsForSpecificAliasAndIndex() throws IOException {
        String alias = "myAlias000";

        createAlias(INDEX_NAME_1, alias);

        AliasExists aliasExists = new AliasExists.Builder().addIndex(INDEX_NAME_1).alias(alias).build();
        JestResult result = client.execute(aliasExists);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    private void createAlias(String index, String alias) {
        final IndicesAliasesRequest.AliasActions aliasAction = IndicesAliasesRequest.AliasActions.add()
                .index(index)
                .alias(alias);
        IndicesAliasesRequest indicesAliasesRequest = new IndicesAliasesRequest().addAliasAction(aliasAction);
        AcknowledgedResponse indicesAliasesResponse =
                client().admin().indices().aliases(indicesAliasesRequest).actionGet(10, TimeUnit.SECONDS);
        assertAcked(indicesAliasesResponse);
    }
}
