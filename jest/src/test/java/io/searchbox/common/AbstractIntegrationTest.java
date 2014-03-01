package io.searchbox.common;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;

/**
 * @author Dogukan Sonmez
 */
@Ignore
public abstract class AbstractIntegrationTest extends ElasticsearchIntegrationTest {

    protected JestClientFactory factory;
    protected JestHttpClient client;

    protected String getPort() {
        return "9200";
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1)
                .put(super.nodeSettings(nodeOrdinal)).build();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        factory = new JestClientFactory();
        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("http://localhost:" + getPort()).multiThreaded(true).build();

        factory.setHttpClientConfig(httpClientConfig);

        client = (JestHttpClient) factory.getObject();
    }

}
