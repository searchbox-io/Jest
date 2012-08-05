package io.searchbox.Indices;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndexTest {

    @Test
    public void createIndexWithoutSettings() {
        CreateIndex createIndex = new CreateIndex("tweet");
        assertEquals("tweet", createIndex.getURI());
        assertEquals("PUT", createIndex.getRestMethodName());
        assertEquals("CREATEINDEX", createIndex.getName());
        Map settingsMap = (Map) createIndex.getData();
        assertTrue(settingsMap.size() == 0);
    }

    @Test
    public void createIndexWithSettingsFromFileSource() {
        final ImmutableSettings.Builder indexerSettings = ImmutableSettings.settingsBuilder();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        CreateIndex createIndex = new CreateIndex("tweet", indexerSettings.build());
        assertEquals("tweet", createIndex.getURI());
        Map settingsMap = (Map) createIndex.getData();
        assertTrue(settingsMap.size() == 3);
    }

    @Test
    public void createIndexWithSourceSettingsFromJSON() throws FileNotFoundException {
        String settingsSource = "/config/elasticsearch-simple.json";
        CreateIndex createIndex = new CreateIndex("searchbox", settingsSource);
        assertEquals("searchbox", createIndex.getURI());
        assertEquals("POST", createIndex.getRestMethodName());
        String settings = "{mappings.type1.properties.field1.type=string, mappings.type1._source.enabled=false, settings.number_of_shards=1, mappings.type1.properties.field1.index=not_analyzed}";
        assertEquals(settings, createIndex.getData().toString());

    }

}
