package io.searchbox.client;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.exception.NoServerConfiguredException;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import org.apache.commons.lang3.tuple.Pair;
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

    // server pool = Pair of (pool size, pool iterator)
    private final AtomicReference<Pair<Integer, Iterator<String>>> serverPoolReference =
            new AtomicReference<Pair<Integer, Iterator<String>>>(Pair.<Integer, Iterator<String>>of(0, ImmutableSet.<String>of().iterator()));
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
        serverPoolReference.set(Pair.of(servers.size(), Iterators.cycle(servers)));

        if (servers.isEmpty()) {
            log.warn("No servers are currently available to connect.");
        } else if (log.isDebugEnabled()) {
            log.debug("Server pool was updated to contain {} servers.", servers.size());
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
        Iterator<String> iterator = serverPoolReference.get().getValue();
        synchronized (iterator) {
            if (iterator.hasNext()) return iterator.next();
            else throw new NoServerConfiguredException("No Server is assigned to client to connect");
        }
    }

    protected int getServerPoolSize() {
        return serverPoolReference.get().getKey();
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

    public Pair<Integer, Iterator<String>> getServerPoolReferencePair() {
        return serverPoolReference.get();
    }

}
