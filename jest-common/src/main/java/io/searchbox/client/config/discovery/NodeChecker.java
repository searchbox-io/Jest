package io.searchbox.client.config.discovery;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.cluster.NodesInfo;

/**
 * Discovers new nodes by calling NodesInfo API on the next available server
 * and parses the <code>nodes</code> object in response to get http publish
 * address.
 */
public class NodeChecker extends AbstractScheduledService {

    private final static Logger log = Logger.getLogger(NodeChecker.class.getName());
    private final static String PUBLISH_ADDRESS_KEY = "http_address";
    private final static Pattern INETSOCKETADDRESS_PATTERN = Pattern.compile("(?:inet\\[)(?:(?:[^:]+)?\\/)?([^:]+):(\\d+)\\]");

    private final NodesInfo action = new NodesInfo.Builder().withHttp().build();

    protected JestClient client;
    protected Scheduler scheduler;
    protected String defaultScheme;

    public NodeChecker(JestClient jestClient, ClientConfig clientConfig) {
        this(jestClient, clientConfig.getDefaultSchemeForDiscoveredNodes(), clientConfig.getDiscoveryFrequency(), clientConfig.getDiscoveryFrequencyTimeUnit());
    }

    public NodeChecker(JestClient jestClient, String defaultScheme, Long discoveryFrequency, TimeUnit discoveryFrequencyTimeUnit) {
        this.client = jestClient;
        this.defaultScheme = defaultScheme;
        this.scheduler = Scheduler.newFixedDelaySchedule(
                0l,
                discoveryFrequency,
                discoveryFrequencyTimeUnit
        );
    }

    @Override
    protected void runOneIteration() throws Exception {

        JestResult result;
        try {
            result = client.execute(action);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error executing NodesInfo!", e);
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
            log.info(MessageFormat.format("Discovered {} HTTP hosts: {}", httpHosts.size(), StringUtils.join(httpHosts, ",")));
            client.setServers(httpHosts);
        } else {
            log.warning(MessageFormat.format("NodesInfo request resulted in error: {}", result.getErrorMessage()));
        }
    }

    @Override
    protected Scheduler scheduler() {
        return scheduler;
    }

    /**
     * Converts the Elasticsearch reported publish address in the format "inet[&lt;hostnamegt;:&lt;port&gt;]" or
     * "inet[&lt;hostname&gt;/&lt;hostaddressgt;:&lt;port&gt;]" to a normalized http address in the form "http://host:port".
     */
    protected String getHttpAddress(String httpAddress) {
        Matcher resolvedMatcher = INETSOCKETADDRESS_PATTERN.matcher(httpAddress);
        if (resolvedMatcher.matches()) {
            return defaultScheme + resolvedMatcher.group(1) + ":" + resolvedMatcher.group(2);
        }

        return null;
    }

}
