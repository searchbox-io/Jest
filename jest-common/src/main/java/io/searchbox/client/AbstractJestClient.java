package io.searchbox.client;


import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.exception.NoServerConfiguredException;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dogukan Sonmez
 */
public abstract class AbstractJestClient implements JestClient {

    public static final String ELASTIC_SEARCH_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    protected Gson gson = new GsonBuilder()
            .setDateFormat(ELASTIC_SEARCH_DATE_FORMAT)
            .create();

    private final static Logger log = LoggerFactory.getLogger(AbstractJestClient.class);

    private final AtomicReference<ServerPool> serverPoolReference =
            new AtomicReference<ServerPool>(new ServerPool(ImmutableSet.<String>of()));
    private NodeChecker nodeChecker;
    private IdleConnectionReaper idleConnectionReaper;
    private boolean requestCompressionEnabled;

    public void setNodeChecker(NodeChecker nodeChecker) {
        this.nodeChecker = nodeChecker;
    }

    public void setIdleConnectionReaper(IdleConnectionReaper idleConnectionReaper) {
        this.idleConnectionReaper = idleConnectionReaper;
    }

    public void setServers(Set<String> servers) {
        if (servers.equals(serverPoolReference.get().getServers())) {
            if (log.isDebugEnabled()) {
                log.debug("Server pool already contains same list of servers: {}",
                        Joiner.on(',').join(scrubServerURIs(servers)));
            }
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Setting server pool to a list of {} servers: [{}]",
                      servers.size(), Joiner.on(',').join(scrubServerURIs(servers)));
        }
        serverPoolReference.set(new ServerPool(servers));

        if (servers.isEmpty()) {
            log.warn("No servers are currently available to connect.");
        }
    }

    @VisibleForTesting
    Set<String> scrubServerURIs(Set<String> servers) {
        final ImmutableSet.Builder<String> scrubbedServers = ImmutableSet.builder();
        for (String server : servers) {
            final URI originalURI = URI.create(server);
            try {
                final URI scrubbedURI = new URI(originalURI.getScheme(),
                        null, // Remove user info
                        originalURI.getHost(),
                        originalURI.getPort(),
                        originalURI.getPath(),
                        originalURI.getQuery(),
                        originalURI.getFragment());
                scrubbedServers.add(scrubbedURI.toString());
            } catch (URISyntaxException e) {
                log.debug("Couldn't scrub server URI " + originalURI, e);
            }
        }
        return scrubbedServers.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownClient() {
        try {
            close();
        } catch (IOException e) {
            log.error("Error while shutting down client", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (null != nodeChecker) {
            nodeChecker.stopAsync();
            nodeChecker.awaitTerminated();
        }
        if (null != idleConnectionReaper) {
            idleConnectionReaper.stopAsync();
            idleConnectionReaper.awaitTerminated();
        }
    }

    /**
     * @throws io.searchbox.client.config.exception.NoServerConfiguredException
     */
    protected String getNextServer() {
        return serverPoolReference.get().getNextServer();
    }

    protected int getServerPoolSize() {
        return serverPoolReference.get().getSize();
    }

    protected String getRequestURL(String elasticSearchServer, String uri) {
        StringBuilder sb = new StringBuilder(elasticSearchServer);

        if (uri.length() > 0 && uri.charAt(0) == '/') sb.append(uri);
        else sb.append('/').append(uri);

        return sb.toString();
    }

    public boolean isRequestCompressionEnabled() {
        return requestCompressionEnabled;
    }

    public void setRequestCompressionEnabled(boolean requestCompressionEnabled) {
        this.requestCompressionEnabled = requestCompressionEnabled;
    }

    private static final class ServerPool {
        private final List<String> serversRing;
        private final AtomicInteger nextServerIndex = new AtomicInteger(0);

        public ServerPool(final Set<String> servers) {
            this.serversRing = ImmutableList.copyOf(servers);
        }

        public Set<String> getServers() {
            return ImmutableSet.copyOf(serversRing);
        }

        public String getNextServer() {
            if (serversRing.size() > 0) {
                try {
                    return serversRing.get(nextServerIndex.getAndIncrement() % serversRing.size());
                } catch (IndexOutOfBoundsException outOfBoundsException) {
                    // In the very rare case where nextServerIndex overflowed, this will end up with a negative number,
                    // resulting in an IndexOutOfBoundsException.
                    // We should then start back at the beginning of the server list.
                    // Note that this might happen on several threads at once, in which the reset might happen a few times
                    log.info("Resetting next server index");
                    nextServerIndex.set(0);
                    return serversRing.get(nextServerIndex.getAndIncrement() % serversRing.size());
                }
            }
            else {
                throw new NoServerConfiguredException("No Server is assigned to client to connect");
            }
        }

        public int getSize() {
            return serversRing.size();
        }
    }
}
