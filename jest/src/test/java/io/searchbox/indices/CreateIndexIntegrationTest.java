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
        final Settings.Builder indexerSettings = Settings.settingsBuilder();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        Map<String, String> expectedSettingsMap = indexerSettings.build().getAsMap();
        CreateIndex createIndex = new CreateIndex.Builder(index)
                .settings(expectedSettingsMap)
                .build();

        JestResult result = client.execute(createIndex);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetSettingsResponse settingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest().indices(index)).actionGet();
        assertNotNull(settingsResponse);
        Settings actualSettingsMap = settingsResponse.getIndexToSettings().get(index);
        for (Map.Entry<String, String> entry : expectedSettingsMap.entrySet()) {
            String key = "index." + entry.getKey();
            assertEquals(entry.getValue(), actualSettingsMap.get(key));
        }
    }

    @Test
    public void createIndexWithStringSettingsAndMapping() throws IOException {
        String index = "stringyone";
        String expectedType1Maping =
                "\"_source\":{\"enabled\":false},\"properties\":{\"field1\":{\"type\":\"string\",\"index\":\"not_analyzed\"}}";
        String settingsJson = "{\n" +
                "    \"settings\" : {\n" +
                "        \"number_of_shards\" : 8\n" +
                "    },\n" +
                "    \"mappings\" : {\"type1\": {" + expectedType1Maping + "}}" +
                "}";
        CreateIndex createIndex = new CreateIndex.Builder(index)
                .settings(settingsJson)
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
        assertEquals("string", ((Map) ((Map) actualType1Mapping.get("properties")).get("field1")).get("type"));
        assertEquals("not_analyzed", ((Map)((Map)actualType1Mapping.get("properties")).get("field1")).get("index"));
    }

}
