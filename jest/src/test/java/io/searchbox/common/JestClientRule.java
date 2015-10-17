package io.searchbox.common;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.elasticsearch.test.TestCluster;
import org.junit.rules.ExternalResource;

import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;

public class JestClientRule extends ExternalResource {

    private final JestClientFactory factory = new JestClientFactory();
    private JestHttpClient client;
    private final Supplier<TestCluster> cluster;

    public JestClientRule(Supplier<TestCluster> cluster) {
        this.cluster = cluster;
    }

    protected int getPort() {
        assertTrue("There should be at least 1 HTTP endpoint exposed in the test cluster",
                cluster.get().httpAddresses().length > 0);
        return cluster.get().httpAddresses()[0].getPort();
    }

    @Override
    protected void before() throws Throwable {
        factory.setHttpClientConfig(
                new HttpClientConfig
                        .Builder("http://localhost:" + getPort())
                        .multiThreaded(true).build()
        );
        client = (JestHttpClient) factory.getObject();
    }

    @Override
    protected void after() {
        client.shutdownClient();
        client = null;
    }

    public JestHttpClient getClient() {
        return client;
    }
}
