package io.searchbox.client.config.discovery;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.cluster.NodesInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Discovers new nodes by calling NodesInfo API on the next available server
 * and parses the <code>nodes</code> object in response to get http publish
 * address.
 */
public class NodeChecker extends AbstractScheduledService {

    private final static Logger log = LoggerFactory.getLogger(NodeChecker.class);
    private final static String PUBLISH_ADDRESS_KEY = "http_address";
    private final static Pattern INETSOCKETADDRESS_PATTERN = Pattern.compile("(?:inet\\[)?(?:(?:[^:]+)?\\/)?([^:]+):(\\d+)\\]?");

    private final NodesInfo action = new NodesInfo.Builder().withHttp().build();

    protected JestClient client;
    protected Scheduler scheduler;
    protected String defaultScheme;
    protected Set<String> bootstrapServerList;

    public NodeChecker(JestClient jestClient, ClientConfig clientConfig) {
        this(jestClient, clientConfig.getDefaultSchemeForDiscoveredNodes(), clientConfig.getDiscoveryFrequency(), clientConfig.getDiscoveryFrequencyTimeUnit(),
				clientConfig.getServerList());
    }

    public NodeChecker(JestClient jestClient, String defaultScheme, Long discoveryFrequency, TimeUnit discoveryFrequencyTimeUnit, Set<String> servers) {
        this.client = jestClient;
        this.defaultScheme = defaultScheme;
        this.scheduler = Scheduler.newFixedDelaySchedule(
                0l,
                discoveryFrequency,
                discoveryFrequencyTimeUnit
        );
		this.bootstrapServerList = ImmutableSet.copyOf(servers);
    }

    @Override
    protected void runOneIteration() throws Exception {
        JestResult result;
        try {
            result = client.execute(action);
        } catch (Exception e) {
            log.error("Error executing NodesInfo!", e);
            client.setServers(bootstrapServerList);
            return;
            // do not elevate the exception since that will stop the scheduled calls.
            // throw new RuntimeException("Error executing NodesInfo!", e);
        }

        if (result.isSucceeded()) {
            LinkedHashSet<String> httpHosts = new LinkedHashSet<String>();

            JsonObject jsonMap = result.getJsonObject();
            JsonObject nodes = (JsonObject) jsonMap.get("nodes");
            if (nodes != null) {
                for (Entry<String, JsonElement> entry : nodes.entrySet()) {
                    JsonObject host = entry.getValue().getAsJsonObject();

                    // get as a JsonElement first as some nodes in the cluster may not have an http_address
                    if (host.has(PUBLISH_ADDRESS_KEY)) {
                        JsonElement addressElement = host.get(PUBLISH_ADDRESS_KEY);
                        if (!addressElement.isJsonNull()) {
                            String httpAddress = getHttpAddress(addressElement.getAsString());
                            if(httpAddress != null) httpHosts.add(httpAddress);
                        }
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Discovered {} HTTP hosts: {}", httpHosts.size(), StringUtils.join(httpHosts, ","));
            }
            client.setServers(httpHosts);
        } else {
            log.warn("NodesInfo request resulted in error: {}", result.getErrorMessage());
            client.setServers(bootstrapServerList);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return scheduler;
    }

    @Override
    protected ScheduledExecutorService executor() {
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(serviceName())
                .build());
        // Add a listener to shutdown the executor after the service is stopped.  This ensures that the
        // JVM shutdown will not be prevented from exiting after this service has stopped or failed.
        // Technically this listener is added after start() was called so it is a little gross, but it
        // is called within doStart() so we know that the service cannot terminate or fail concurrently
        // with adding this listener so it is impossible to miss an event that we are interested in.
        addListener(new Listener() {
            @Override public void terminated(State from) {
                executor.shutdown();
            }
            @Override public void failed(State from, Throwable failure) {
                executor.shutdown();
            }}, MoreExecutors.directExecutor());
        return executor;
    }

    /**
     * Converts the Elasticsearch reported publish address in the format "inet[<hostname>:<port>]" or
     * "inet[<hostname>/<hostaddress>:<port>]" to a normalized http address in the form "http://host:port".
     */
    protected String getHttpAddress(String httpAddress) {
        Matcher resolvedMatcher = INETSOCKETADDRESS_PATTERN.matcher(httpAddress);
        if (resolvedMatcher.matches()) {
            return defaultScheme + resolvedMatcher.group(1) + ":" + resolvedMatcher.group(2);
        }

        return null;
    }

}
