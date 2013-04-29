package io.searchbox.client.config.discovery;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.cluster.NodesInfo;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * used to discover new nodes
 */
public class NodeChecker extends AbstractScheduledService {

    final static Logger logger = LoggerFactory.getLogger(JestHttpClient.class);

    //actual client config instance
    JestClient client;
    ClientConfig clientConfig;

    public NodeChecker(ClientConfig clientConfig, JestClient jestClient) {
        this.client = jestClient;
        this.clientConfig = clientConfig;
    }

    @Override
    protected void runOneIteration() throws Exception {

        NodesInfo action = new NodesInfo();


        JestResult result;
        try {
            result = client.execute(action);
        } catch (Exception e) {
            logger.error("Error executing NodesInfo!", e);
            throw new RuntimeException("Error executing NodesInfo!", e);
        }

        LinkedHashSet<String> httpHosts = new LinkedHashSet<String>();

        JsonObject jsonMap = result.getJsonObject();
        JsonObject nodes = (JsonObject) jsonMap.get("nodes");
        if (nodes != null) {
            for (Entry<String, JsonElement> entry : nodes.entrySet()) {
                JsonObject host = (JsonObject) entry.getValue();
                String http_address =  host.get("http_address").getAsString();
                if (null != http_address) {
                    String cleanHttpAddress = "http://" + http_address.substring(6, http_address.length() - 1);
                    httpHosts.add(cleanHttpAddress);
                }
            }
        }

        logger.info("Discovered Http Hosts: {}", httpHosts);

        client.setServers(httpHosts);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0l,
                (Long) clientConfig.getProperty(ClientConstants.DISCOVERY_FREQUENCY),
                (TimeUnit) clientConfig.getProperty(ClientConstants.DISCOVERY_FREQUENCY_TIMEUNIT));
    }
}
