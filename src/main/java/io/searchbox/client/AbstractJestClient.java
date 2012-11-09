package io.searchbox.client;


import com.google.common.collect.Iterators;
import com.google.gson.Gson;
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

    public LinkedHashSet<String> getServers() {
        return servers;
    }

    public void setServers(LinkedHashSet<String> servers) {
        this.servers = servers;
        this.roundRobinIterator = Iterators.cycle(servers);
    }

    public void shutdownClient() {
    }

    protected String getElasticSearchServer() {
        if (roundRobinIterator.hasNext())
            return roundRobinIterator.next();
        throw new RuntimeException("No Server is assigned to client to connect");
    }

    protected JestResult createNewElasticSearchResult(String json, StatusLine statusLine, String requestName, String pathToResult) {
        JestResult result = new JestResult();
        Map jsonMap = convertJsonStringToMapObject(json);
        result.setJsonString(json);
        result.setJsonMap(jsonMap);
        result.setPathToResult(pathToResult);

        if ((statusLine.getStatusCode() / 100) == 2) {
            if (!isOperationSucceed(jsonMap, requestName)) {
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

    protected boolean isOperationSucceed(Map json, String requestName) {
        try {
            if (requestName.equalsIgnoreCase("INDEX")) {
                return (Boolean) json.get("ok");
            } else if ((requestName.equalsIgnoreCase("DELETE"))) {
                boolean foundOrAcknowledged = json.containsKey("found") ? (Boolean) json.get("found") :  json.containsKey("acknowledged") ? (Boolean) json.get("acknowledged") : false;
                return ((Boolean) json.get("ok") && foundOrAcknowledged);
            } else if (requestName.equalsIgnoreCase("UPDATE")) {
                return (Boolean) json.get("ok");
            } else if (requestName.equalsIgnoreCase("GET")) {
                return (Boolean) json.get("exists");
            } else if (requestName.equalsIgnoreCase("DELETE_INDEX")) {
                return ((Boolean) json.get("ok") && (Boolean) json.get("acknowledged"));
            }
        } catch (Exception e) {
            log.error("Exception occurred during the parsing result. Since http ok going to return isSucceed as a true", e);
        }
        return true;
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