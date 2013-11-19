package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class CreateIndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createIndexWithDefaultSettings() {
        CreateIndex createIndex = new CreateIndex.Builder("newindex").build();
        try {
            executeTestCase(createIndex);
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

    @Test
    public void createIndexWithSettings() {
        final ImmutableSettings.Builder indexerSettings = ImmutableSettings.settingsBuilder();
        indexerSettings.put("analysis.analyzer.events.type", "custom");
        indexerSettings.put("analysis.analyzer.events.tokenizer", "standard");
        indexerSettings.put("analysis.analyzer.events.filter", "snowball, standard, lowercase");
        CreateIndex createIndex = new CreateIndex.Builder("anothernewindex")
                .settings(indexerSettings.build().getAsMap())
                .build();
        try {
            executeTestCase(createIndex);
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

    private void executeTestCase(CreateIndex createIndex) throws RuntimeException, IOException {
        JestResult result = client.execute(createIndex);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
