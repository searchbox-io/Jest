package io.searchbox.client;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.searchbox.action.Action;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testGetElasticSearchServerWithCredentials() {
        final Set<String> set = ImmutableSet.of(
                "http://user:pass@localhost:9200",
                "http://user:pass@localhost:9300",
                "http://user:pass@localhost:9400");
        client.setServers(set);

        Set<String> serverList = new HashSet<>();

        for (int i = 0; i < set.size(); i++) {
            serverList.add(client.getNextServer());
        }

        assertEquals("round robin does not work", 3, serverList.size());

        assertTrue(set.contains("http://user:pass@localhost:9200"));
        assertTrue(set.contains("http://user:pass@localhost:9300"));
        assertTrue(set.contains("http://user:pass@localhost:9400"));
    }

    @Test
    public void testScrubServerURIs() {
        final Set<String> set = ImmutableSet.of(
                "http://user:pass@localhost:9200",
                "http://localhost:9300/path?query#fragment");
        final Set<String> scrubbedURIs = client.scrubServerURIs(set);

        assertEquals(2, scrubbedURIs.size());
        assertTrue(scrubbedURIs.contains("http://localhost:9200"));
        assertTrue(scrubbedURIs.contains("http://localhost:9300/path?query#fragment"));
    }

    @Test
    public void testGetElasticSearchServerIsThreadsafe() throws Exception {
        final int NUM_THREADS = 12;
        final int NUM_ITERATIONS = 12000;
        final int MIN_ACCEPTABLE_PER_SERVER = 3900;
        final int MAX_ACCEPTABLE_PER_SERVER = 4100;

        // do NUM_ITERATIONS of getNextServer, across NUM_THREADS
        // we should ensure that no exceptions are thrown,
        // and that we get a rather even share of results for each possible server

        final Set<String> servers = new LinkedHashSet<String>();
        servers.add("http://localhost:9200");
        servers.add("http://localhost:9300");
        servers.add("http://localhost:9400");
        client.setServers(servers);

        final Map<String, AtomicInteger> hits = Maps.newConcurrentMap();
        for (String server : servers) {
            hits.put(server, new AtomicInteger());
        }

        final AtomicInteger numExceptions = new AtomicInteger();
        final ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_ITERATIONS; i++) {
            if (numExceptions.get() == 0) { // don't bother submitting more if there are exceptions already
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String nextServer = client.getNextServer();
                            if (nextServer == null) {
                                throw new IllegalStateException("acquired null server!");
                            } else if (!hits.containsKey(nextServer)) {
                                throw new IllegalStateException("acquired server " + nextServer + ", but this is unknown!");
                            } else {
                                hits.get(nextServer).incrementAndGet();
                            }
                        } catch (Throwable t) {
                            System.err.println("Error occurred: " + t.getMessage());
                            t.printStackTrace();
                            numExceptions.incrementAndGet();
                        }
                    }
                });
            } else {
                System.err.println("Exception detected, not submitting more.");
                break;
            }
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals("should see 0 exceptions", 0, numExceptions.get());

        System.out.println(hits);
        for (Map.Entry<String, AtomicInteger> entry : hits.entrySet()) {
            assertTrue("should have roughly same entries in each of the hits buckets but " + entry.getKey() + " has " + entry.getValue(),
                    entry.getValue().get() >= MIN_ACCEPTABLE_PER_SERVER && entry.getValue().get() <= MAX_ACCEPTABLE_PER_SERVER);
        }
    }
}
