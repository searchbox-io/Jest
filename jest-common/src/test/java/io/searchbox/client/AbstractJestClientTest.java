package io.searchbox.client;

import io.searchbox.action.Action;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */

public class AbstractJestClientTest {

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

        String node1 = "http://localhost:9200";
        String node2 = "http://localhost:9300";
        String node3 = "http://localhost:9400";

        set.add(node1);
        set.add(node2);
        set.add(node3);
        client.setServers(set);

        Set<String> serverList = new HashSet<String>();
        for (int i = 0; i < 3; i++) {
            serverList.add(client.getNextServer());
        }

        assertEquals("round robin does not work", 3, serverList.size());

        assertTrue(set.contains(node1));
        assertTrue(set.contains(node2));
        assertTrue(set.contains(node3));
    }

    @Test
    public void testServerPoolReferencePair() throws Exception {
        LinkedHashSet<String> set = new LinkedHashSet<String>();

        String node1 = "http://localhost:9200";
        String node2 = "http://localhost:9300";
        String node3 = "http://localhost:9400";

        set.add(node1);
        set.add(node2);
        set.add(node3);
        client.setServers(set);

        Pair<Integer, Iterator<String>> serverPoolReference = client.getServerPoolReferencePair();

        Set<String> serverList = new HashSet<String>();

        for (int i = 0; i < serverPoolReference.getKey(); i++) {
            serverList.add(serverPoolReference.getValue().next());
        }

        assertEquals("could not print serverList", 3, serverList.size());

        assertTrue(set.contains(node1));
        assertTrue(set.contains(node2));
        assertTrue(set.contains(node3));
    }

}
