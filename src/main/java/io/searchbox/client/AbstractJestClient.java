package io.searchbox.client;


import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import io.searchbox.Action;
import io.searchbox.client.config.discovery.NodeChecker;
import org.apache.http.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public abstract class AbstractJestClient implements JestClient {

    final static Logger log = LoggerFactory.getLogger(AbstractJestClient.class);

    public LinkedHashSet<String> servers;

    private Iterator<String> roundRobinIterator;

    private NodeChecker nodeChecker;

    public void setNodeChecker(NodeChecker nodeChecker) {
        this.nodeChecker = nodeChecker;
    }

    public LinkedHashSet<String> getServers() {
        return servers;
    }

    public void setServers(LinkedHashSet<String> servers) {
        this.servers = servers;
        this.roundRobinIterator = Iterators.cycle(servers);
    }

    public void shutdownClient() {
        if (null != nodeChecker)
            nodeChecker.stop();
    }

    protected synchronized String getElasticSearchServer() {
        if (roundRobinIterator.hasNext())
            return roundRobinIterator.next();
        throw new RuntimeException("No Server is assigned to client to connect");
    }

    protected JestResult createNewElasticSearchResult(String json, StatusLine statusLine, Action clientRequest) {
        JestResult result = new JestResult();
        Map jsonMap = convertJsonStringToMapObject(json);
        result.setJsonString(json);
        result.setJsonMap(jsonMap);
        result.setPathToResult(clientRequest.getPathToResult());

        if ((statusLine.getStatusCode() / 100) == 2) {
            if (!clientRequest.isOperationSucceed(jsonMap)) {
                result.setSucceeded(false);
                log.debug("http request was success but operation is failed Status code in 200");
            } else {
                result.setSucceeded(true);
                log.debug("Request and operation succeeded");
            }
        } else {
            result.setSucceeded(false);
            log.debug("Response is failed");
        }
        return result;
    }

    protected Map convertJsonStringToMapObject(String jsonTxt) {
        try {
            return new Gson().fromJson(jsonTxt, Map.class);
        } catch (Exception e) {
            log.error("An exception occurred while converting json string to map object");
        }
        return new HashMap();
    }

    protected String getRequestURL(String elasticSearchServer, String uri) {
        String serverUrl = elasticSearchServer.endsWith("/") ?
                elasticSearchServer.substring(0, elasticSearchServer.length() - 1) : elasticSearchServer;

        StringBuilder sb = new StringBuilder(serverUrl);

        sb.append(uri.startsWith("/") ? uri : "/" + uri);
        return sb.toString();
    }
}