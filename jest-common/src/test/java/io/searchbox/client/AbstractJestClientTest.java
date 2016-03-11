package io.searchbox.client;

import io.searchbox.action.Action;
import io.searchbox.client.config.exception.NoServerConfiguredException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */

public class AbstractJestClientTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    AbstractJestClient client = new AbstractJestClient() {
        @Override
        public <T extends JestResult> T execute(Action<T> clientRequest) throws IOException {
            return null;  // NOOP
        }

        @Override
        public <T extends JestResult> void executeAsync(final Action<T> clientRequest, final JestResultHandler<? super T> resultHandler) {
            // NOOP
        }
    };

    @Test
    public void getRequestURL() {
        String requestURI = "twitter/tweet/1";
        String elasticSearchServer = "http://localhost:9200";
        assertEquals("http://localhost:9200/twitter/tweet/1", client.getRequestURL(elasticSearchServer, requestURI));
    }

    @Test
    public void testGetElasticSearchServer() throws Exception {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("http://localhost:9200");
        set.add("http://localhost:9300");
        set.add("http://localhost:9400");
        client.setServers(set);

        List<String> serverList = new ArrayList<String>();

        for (int i = 0; i < 3; i++) {
            serverList.add(client.getNextServer());
        }

        assertEquals("round robin does not work", 3, serverList.size());
        assertEquals("pool size is wrong", 3, client.getServerPoolSize());

        assertEquals("http://localhost:9200", serverList.get(0));
        assertEquals("http://localhost:9300", serverList.get(1));
        assertEquals("http://localhost:9400", serverList.get(2));

        assertTrue(set.contains("http://localhost:9200"));
        assertTrue(set.contains("http://localhost:9300"));
        assertTrue(set.contains("http://localhost:9400"));
    }

    @Test
    public void testGetElasticSearchServerNoServerAvailable() throws Exception {
        assertEquals("pool size is wrong", 0, client.getServerPoolSize());
        exception.expect(NoServerConfiguredException.class);
        client.getNextServer();

        LinkedHashSet<String> set = new LinkedHashSet<String>();
        client.setServers(set);

        assertEquals("pool size is wrong", 0, client.getServerPoolSize());
        exception.expect(NoServerConfiguredException.class);
        client.getNextServer();
    }
}
