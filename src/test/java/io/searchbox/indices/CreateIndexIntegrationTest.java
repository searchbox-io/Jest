package io.searchbox.indices;


import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileNotFoundException;
import java.io.IOException;

import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndexIntegrationTest {

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;


    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void createIndexWithDefaultSettings() {
        CreateIndex createIndex = new CreateIndex("newindex");
        try {
            client.execute(createIndex);
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
        CreateIndex createIndex = new CreateIndex("anothernewindex", indexerSettings.build());
        try {
            client.execute(createIndex);
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

    @Test
    public void createIndexWithSettingsFromJsonSourceFile() throws FileNotFoundException {
        CreateIndex createIndex = new CreateIndex("indexfromjson", "/config/elasticsearch-simple.json");
        try {
            client.execute(createIndex);
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }


}
