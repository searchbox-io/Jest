package io.searchbox.common;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.internal.InternalNode;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import io.searchbox.rs.client.JestClientFactory;
import io.searchbox.rs.client.config.RsClientConfig;
import io.searchbox.rs.client.http.JestRsClient;

/**
 * @author Dogukan Sonmez
 */
@Ignore
public abstract class AbstractIntegrationTest extends ElasticsearchIntegrationTest {

    protected final JestClientFactory factory = new JestClientFactory();
    protected JestRsClient client;

    protected int getPort() {
        assertTrue("There should be at least 1 HTTP endpoint exposed in the test cluster",
                cluster().httpAddresses().length > 0);
        return cluster().httpAddresses()[0].getPort();
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 1)
                .put(RestController.HTTP_JSON_ENABLE, true)
                .put(InternalNode.HTTP_ENABLED, true)
                .build();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        factory.setHttpClientConfig(
                new RsClientConfig
                        .Builder("http://localhost:" + getPort())
                        .multiThreaded(true).build()
        );
        client = (JestRsClient) factory.getObject();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        client.shutdownClient();
        client = null;
    }

}
