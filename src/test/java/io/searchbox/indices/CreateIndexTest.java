package io.searchbox.indices;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.Test;

import com.google.gson.Gson;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndexTest {

    @Test
    public void createIndexWithoutSettings() {
        CreateIndex createIndex = new CreateIndex("tweet");
        assertEquals("tweet", createIndex.getURI());
        assertEquals("PUT", createIndex.getRestMethodName());
        String settings = new Gson().toJson(createIndex.getData());
        assertEquals("", settings);
    }

    @Test
    public void createIndexWithSettingsFromFileSource() {
        final ImmutableSettings.Builder indexerSettings = ImmutableSettings.settingsBuilder();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        CreateIndex createIndex = new CreateIndex("tweet", indexerSettings.build().getAsMap());
        assertEquals("tweet", createIndex.getURI());
        Map settingsMap = (Map) createIndex.getData();
        assertTrue(settingsMap.size() == 3);
    }
}
