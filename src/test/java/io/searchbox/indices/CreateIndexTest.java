package io.searchbox.indices;

import com.google.gson.Gson;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */
public class CreateIndexTest {

    @Test
    public void createIndexWithoutSettings() {
        CreateIndex createIndex = new CreateIndex.Builder("tweet").build();
        assertEquals("tweet", createIndex.getURI());
        assertEquals("PUT", createIndex.getRestMethodName());
        String settings = new Gson().toJson(createIndex.getData(null));
        assertTrue("", settings.equals("") || settings.equals("{}"));
    }

    @Test
    public void createIndexWithSettingsFromFileSource() {
        final ImmutableSettings.Builder indexerSettings = ImmutableSettings.settingsBuilder();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        CreateIndex createIndex = new CreateIndex.Builder("tweet").settings(indexerSettings.build().getAsMap()).build();
        assertEquals("tweet", createIndex.getURI());
        Map settingsMap = (Map) createIndex.getData(null);
        assertTrue(settingsMap.size() == 3);
    }
}
