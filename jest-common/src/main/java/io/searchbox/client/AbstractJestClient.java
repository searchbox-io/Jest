package io.searchbox.client;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.exception.NoServerConfiguredException;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;
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
                        StringUtils.join(servers, ","));
            }
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Setting server pool to a list of {} servers: [{}]",
                      servers.size(), StringUtils.join(servers, ","));
        }
        serverPoolReference.set(new ServerPool(servers));

        if (servers.isEmpty()) {
            log.warn("No servers are currently available to connect.");
        } else if (log.isDebugEnabled()) {
            log.info("Server pool was updated to contain {} servers: [{}].",
                    servers.size(), StringUtils.join(servers, ","));
        }
    }

    public void shutdownClient() {
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
        private final Set<String> servers;
        private final Iterator<String> serverIterator;

        public ServerPool(final Set<String> servers) {
            this.servers = ImmutableSet.copyOf(servers);
            this.serverIterator = Iterators.cycle(servers);
        }

        public Set<String> getServers() {
            return servers;
        }

        public String getNextServer() {
            if (serverIterator.hasNext()) return serverIterator.next();
            else throw new NoServerConfiguredException("No Server is assigned to client to connect");
        }

        public int getSize() {
            return servers.size();
        }

    }
}
