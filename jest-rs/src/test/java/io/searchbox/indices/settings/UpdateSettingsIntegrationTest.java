package io.searchbox.indices.settings;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;

/**
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
public class UpdateSettingsIntegrationTest extends AbstractIntegrationTest {
    private static final String INDEX_1 = "updatesettingstest1";
    private static final String INDEX_2 = "updatesettingstest2";

    @Before
    public void init() {
        createIndex(INDEX_1, INDEX_2);
        ensureGreen(INDEX_1, INDEX_2);
    }

    @Test
    public void testBasicFlowForAllIndices() throws IOException {
        GetSettingsResponse getSettingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest()).actionGet();
        Integer originalNumberOfReplicas = Integer.parseInt(getSettingsResponse.getSetting(INDEX_1, "index.number_of_replicas"));
        Integer expectedNumberOfReplicas = originalNumberOfReplicas + 1;

        String body = "{ \"index\" : { " +
                "\"number_of_replicas\" : " + expectedNumberOfReplicas.toString() +
                "} }";

        UpdateSettings updateSettings = new UpdateSettings.Builder(body).build();
        JestResult result = client.execute(updateSettings);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        getSettingsResponse = client().admin().indices().getSettings(new GetSettingsRequest()).actionGet();
        Integer actualNumberOfReplicasForIndex1 = Integer.parseInt(getSettingsResponse.getSetting(INDEX_1, "index.number_of_replicas"));
        Integer actualNumberOfReplicasForIndex2 = Integer.parseInt(getSettingsResponse.getSetting(INDEX_2, "index.number_of_replicas"));

        assertEquals(expectedNumberOfReplicas, actualNumberOfReplicasForIndex1);
        assertEquals(expectedNumberOfReplicas, actualNumberOfReplicasForIndex2);
    }

    @Test
    public void testBasicFlowForNonExistingIndex() throws IOException {
        String body = "{ \"index\" : { " +
                "\"number_of_replicas\" : 3" +
                "} }";

        UpdateSettings updateSettings = new UpdateSettings.Builder(body).addIndex("idontexist").build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void testBasicFlowForTargetedIndex() throws IOException {
        GetSettingsResponse getSettingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest()).actionGet();
        Integer originalNumberOfReplicasForIndex1 = Integer.parseInt(getSettingsResponse.getSetting(INDEX_1, "index.number_of_replicas"));
        Integer originalNumberOfReplicasForIndex2 = Integer.parseInt(getSettingsResponse.getSetting(INDEX_2, "index.number_of_replicas"));
        Integer expectedNumberOfReplicasForIndex1 = originalNumberOfReplicasForIndex1 + 1;

        String body = "{ \"index\" : { " +
                "\"number_of_replicas\" : " + expectedNumberOfReplicasForIndex1.toString() +
                "} }";

        UpdateSettings updateSettings = new UpdateSettings.Builder(body).addIndex(INDEX_1).build();
        JestResult result = client.execute(updateSettings);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        getSettingsResponse = client().admin().indices().getSettings(new GetSettingsRequest()).actionGet();
        Integer actualNumberOfReplicasForIndex1 = Integer.parseInt(getSettingsResponse.getSetting(INDEX_1, "index.number_of_replicas"));
        Integer actualNumberOfReplicasForIndex2 = Integer.parseInt(getSettingsResponse.getSetting(INDEX_2, "index.number_of_replicas"));

        assertEquals(expectedNumberOfReplicasForIndex1, actualNumberOfReplicasForIndex1);
        assertEquals(originalNumberOfReplicasForIndex2, actualNumberOfReplicasForIndex2);
    }

    /**
     * No content is sent rather than "" when built with <code>""</code>.
     */
    @Ignore
    @Test
    public void testWithEmptySource() throws IOException {
        UpdateSettings updateSettings = new UpdateSettings.Builder("").addIndex(INDEX_1).build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

    @Test
    public void testWithEmptyJsonSource() throws IOException {
        UpdateSettings updateSettings = new UpdateSettings.Builder("{}").addIndex(INDEX_1).build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

    /**
     * No content is sent rather than null when built with <code>null</code>.
     */
    @Ignore
    @Test
    public void testWithNullSource() throws IOException {
        UpdateSettings updateSettings = new UpdateSettings.Builder(null).addIndex(INDEX_1).build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

}
