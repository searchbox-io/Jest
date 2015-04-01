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

    private final static Logger logger = LoggerFactory.getLogger(NodeChecker.class);
    private final NodesInfo action = new NodesInfo.Builder().build();
    //actual client config instance
    JestClient client;
    ClientConfig clientConfig;

    public NodeChecker(ClientConfig clientConfig, JestClient jestClient) {
        this.client = jestClient;
        this.clientConfig = clientConfig;
    }

    @Override
    protected void runOneIteration() throws Exception {
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

                    // get as a JsonElement first as some nodes in the cluster may not have an http_address
                    JsonElement addressElement = host.get("http_address");
                    if (null != addressElement) {
                        String http_address = addressElement.getAsString();
                        String cleanHttpAddress = "http://" + http_address.substring(5, http_address.length() - 1);
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
