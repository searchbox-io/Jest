package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class CreateIndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createIndexWithDefaultSettings() throws IOException {
        CreateIndex createIndex = new CreateIndex.Builder("newindex").build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void createIndexWithMapSettings() throws IOException {
        String index = "anothernewindex";

        Map<String, Object> indexerSettings = new HashMap<>();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "standard, lowercase");

        Map<String, Object> expectedSettingsMap = indexerSettings;
        CreateIndex createIndex = new CreateIndex.Builder(index)
                .settings(expectedSettingsMap)
                .build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetSettingsResponse settingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest().indices(index)).actionGet();
        assertNotNull(settingsResponse);
        Settings actualSettingsMap = settingsResponse.getIndexToSettings().get(index);
        for (Map.Entry<String, Object> entry : expectedSettingsMap.entrySet()) {
            String key = "index." + entry.getKey();
            assertEquals(entry.getValue(), actualSettingsMap.get(key));
        }
    }

    @Test
    public void createIndexWithSettingsMapAndMappingsString() throws IOException {
        String index = "stringyone";

        Map<String, Object> settings = new HashMap<>();
        settings.put("number_of_shards", 8);

        String mappingsJson = "{\"type1\": {\"_source\":{\"enabled\":false},\"properties\":{\"field1\":{\"type\":\"keyword\"}}}}";

        CreateIndex createIndex = new CreateIndex.Builder(index)
                .settings(settings)
                .mappings(mappingsJson)
                .build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetSettingsResponse settingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest().indices(index)).actionGet();
        assertNotNull(settingsResponse);
        assertEquals("8", settingsResponse.getSetting(index, "index.number_of_shards"));

        GetMappingsResponse mappingsResponse =
                client().admin().indices().getMappings(new GetMappingsRequest().indices(index)).actionGet();
        assertNotNull(mappingsResponse);
        Map<String, Object> actualType1Mapping = mappingsResponse.getMappings().get(index).get("type1").getSourceAsMap();
        assertEquals(Boolean.FALSE, ((Map) actualType1Mapping.get("_source")).get("enabled"));
        assertEquals("keyword", ((Map) ((Map) actualType1Mapping.get("properties")).get("field1")).get("type"));
    }
}
