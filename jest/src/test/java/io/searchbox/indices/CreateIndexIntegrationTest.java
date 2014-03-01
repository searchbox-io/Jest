package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class CreateIndexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void createIndexWithDefaultSettings() throws IOException {
        CreateIndex createIndex = new CreateIndex.Builder("newindex").build();
        executeTestCase(createIndex);
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
