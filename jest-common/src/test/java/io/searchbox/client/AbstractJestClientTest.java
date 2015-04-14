package io.searchbox.client;

import io.searchbox.action.Action;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */

public class AbstractJestClientTest {

    AbstractJestClient client = new AbstractJestClient() {
        @Override
        public <T extends JestResult> T execute(Action<T> clientRequest) throws Exception {
            return null;  // NOOP
        }

        @Override
        public <T extends JestResult> void executeAsync(final Action<T> clientRequest, final JestResultHandler<T> resultHandler)
                throws ExecutionException, InterruptedException, IOException {
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

        Set<String> serverList = new HashSet<String>();

        for (int i = 0; i < 3; i++) {
            serverList.add(client.getNextServer());
        }

        assertEquals("round robin does not work", 3, serverList.size());

        assertTrue(set.contains("http://localhost:9200"));
        assertTrue(set.contains("http://localhost:9300"));
        assertTrue(set.contains("http://localhost:9400"));
    }
}
