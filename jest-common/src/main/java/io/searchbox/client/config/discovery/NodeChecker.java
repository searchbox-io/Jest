package io.searchbox.client.config.discovery;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.cluster.NodesInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map.Entry;

/**
 * used to discover new nodes
 */
public class NodeChecker extends AbstractScheduledService {

    final static Logger logger = LoggerFactory.getLogger(NodeChecker.class);
    //actual client config instance
    JestClient client;
    ClientConfig clientConfig;

    public NodeChecker(ClientConfig clientConfig, JestClient jestClient) {
        this.client = jestClient;
        this.clientConfig = clientConfig;
    }

    @Override
    protected void runOneIteration() throws Exception {
        NodesInfo action = new NodesInfo.Builder().build();

        JestResult result = null;
        try {
            result = client.execute(action);
        } catch (Exception e) {
            logger.error("Error executing NodesInfo!", e);
            // do not elevate the exception since that will stop the scheduled calls.
            // throw new RuntimeException("Error executing NodesInfo!", e);
        }

        if (result != null) {
            LinkedHashSet<String> httpHosts = new LinkedHashSet<String>();

            JsonObject jsonMap = result.getJsonObject();
            JsonObject nodes = (JsonObject) jsonMap.get("nodes");
            if (nodes != null) {
                for (Entry<String, JsonElement> entry : nodes.entrySet()) {
                    JsonObject host = (JsonObject) entry.getValue();
                    String http_address = host.get("http_address").getAsString();
                    if (null != http_address) {
                        String cleanHttpAddress = "http://" + http_address.substring(6, http_address.length() - 1);
                        httpHosts.add(cleanHttpAddress);
                    }
                }
            }

            logger.info("Discovered Http Hosts: {}", httpHosts);
            client.setServers(httpHosts);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0l,
                clientConfig.getDiscoveryFrequency(),
                clientConfig.getDiscoveryFrequencyTimeUnit());
    }
}
