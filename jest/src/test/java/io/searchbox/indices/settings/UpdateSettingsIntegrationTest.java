package io.searchbox.indices.settings;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
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

    @Test
    public void testWithNullSource() throws IOException {
        UpdateSettings updateSettings = new UpdateSettings.Builder(null).addIndex(INDEX_1).build();
        JestResult result = client.execute(updateSettings);
        assertFalse(result.isSucceeded());
    }

}
