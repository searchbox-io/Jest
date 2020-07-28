package io.searchbox.common;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.http.HttpTransportSettings;
import org.elasticsearch.painless.PainlessPlugin;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Dogukan Sonmez
 */
@Ignore
public abstract class AbstractIntegrationTest extends ESIntegTestCase {

    protected final JestClientFactory factory = new JestClientFactory();
    protected JestHttpClient client;

    protected String INDEX = "index";

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        Settings.Builder builder = Settings.builder().put(super.nodeSettings(nodeOrdinal));
        builder.put(NetworkModule.HTTP_TYPE_KEY, Netty4Plugin.NETTY_HTTP_TRANSPORT_NAME);
        builder.put(NetworkModule.TRANSPORT_TYPE_KEY, Netty4Plugin.NETTY_TRANSPORT_NAME);
        builder .put(NetworkModule.HTTP_ENABLED.getKey(), true)
                .put(HttpTransportSettings.SETTING_HTTP_PORT.getKey(), randomIntBetween(49152, 65525))
                .put("network.host", "127.0.0.1");
        return builder.build();
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Arrays.asList(Netty4Plugin.class, PainlessPlugin.class, PercolatorPlugin.class, MustachePlugin.class);
    }

    @Override
    protected boolean ignoreExternalCluster() {
        return true;
    }

    @Override
    protected boolean addMockTransportService() {
        return false;
    }

    @Override
    protected Collection<Class<? extends Plugin>> transportClientPlugins() {
        return nodePlugins();
    }

    @Override
    protected Settings transportClientSettings() {
        Settings.Builder builder = Settings.builder().put(super.transportClientSettings());
        builder.put(NetworkModule.TRANSPORT_TYPE_KEY, Netty4Plugin.NETTY_TRANSPORT_NAME);
        return  builder.build();
    }

    protected int getPort() {
        assertTrue("There should be at least 1 HTTP endpoint exposed in the test cluster",
                cluster().httpAddresses().length > 0);
        return cluster().httpAddresses()[0].getPort();
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        factory.setHttpClientConfig(
                new HttpClientConfig
                        .Builder("http://localhost:" + getPort())
                        .readTimeout(10000)
                        .multiThreaded(true).build()
        );
        client = (JestHttpClient) factory.getObject();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        client.close();
        client = null;
    }

    protected boolean documentExists(String index, String type, String id) {
        GetResponse actionGet = client().prepareGet(index, type, id).get();
        return actionGet.isExists();
    }
}
