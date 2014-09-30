package io.searchbox.client;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.searchbox.client.config.RoundRobinServerList;
import io.searchbox.client.config.ServerList;
import io.searchbox.client.config.discovery.NodeChecker;
import io.searchbox.client.config.exception.NoServerConfiguredException;
import io.searchbox.client.config.idle.IdleConnectionReaper;
import io.searchbox.client.util.PaddedAtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Dogukan Sonmez
 */
public abstract class AbstractJestClient implements JestClient {

    final static Logger log = LoggerFactory.getLogger(AbstractJestClient.class);
    public static final String ELASTIC_SEARCH_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ssZ";
    private final PaddedAtomicReference<ServerList> listOfServers = new PaddedAtomicReference<ServerList>();
    protected Gson gson = new GsonBuilder()
            .setDateFormat(ELASTIC_SEARCH_DATE_FORMAT)
            .create();

    private NodeChecker nodeChecker;
    private IdleConnectionReaper idleConnectionReaper;

    public void setNodeChecker(NodeChecker nodeChecker) {
        this.nodeChecker = nodeChecker;
    }

    public void setIdleConnectionReaper(IdleConnectionReaper idleConnectionReaper) {
        this.idleConnectionReaper = idleConnectionReaper;
    }

    public LinkedHashSet<String> getServers() {
        ServerList server = listOfServers.get();
        if (server != null) return new LinkedHashSet<String>(server.getServers());
        else return null;
    }

    public void setServers(ServerList list) {
        listOfServers.set(list);
    }

    public void setServers(Set<String> servers) {
        try {
            RoundRobinServerList serverList = new RoundRobinServerList(servers);
            listOfServers.set(serverList);
        } catch (NoServerConfiguredException noServers) {
            listOfServers.set(null);
            log.warn("No servers are currently available for the client to talk to.");
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

    protected String getElasticSearchServer() {
        ServerList serverList = listOfServers.get();
        if (serverList != null) return serverList.getServer();
        else throw new NoServerConfiguredException("No Server is assigned to client to connect");
    }

    protected String getRequestURL(String elasticSearchServer, String uri) {
        StringBuilder sb = new StringBuilder(elasticSearchServer);

        if (uri.length() > 0 && uri.charAt(0) == '/') sb.append(uri);
        else sb.append('/').append(uri);

        return sb.toString();
    }
}