package io.searchbox.client;


import com.google.gson.Gson;
import io.searchbox.Action;
import org.apache.http.StatusLine;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class AbstractElasticSearchClient implements ElasticSearchClient {

    private static Logger log = Logger.getLogger(AbstractElasticSearchClient.class.getName());

    public LinkedHashSet<String> servers;

    private String defaultIndex;

    private String defaultType;

    public LinkedHashSet<String> getServers() {
        return servers;
    }

    public void setServers(LinkedHashSet<String> servers) {
        this.servers = servers;
    }

    public ElasticSearchResult execute(Action clientRequest) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T executeAsync(Action clientRequest) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void shutdownClient() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getElasticSearchServer() {
        for (String server : getServers()) {
            return server;
        }
        throw new RuntimeException("No Server is assigned to client to connect");
    }

    protected ElasticSearchResult createNewElasticSearchResult(String json, StatusLine statusLine, String requestName,String pathToResult) {
        ElasticSearchResult result = new ElasticSearchResult();
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
                return ((Boolean) json.get("ok") && (Boolean) json.get("found"));
            } else if (requestName.equalsIgnoreCase("UPDATE")) {
                return (Boolean) json.get("ok");
            } else if (requestName.equalsIgnoreCase("GET")) {
                return (Boolean) json.get("exists");
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

    protected String getRequestURL(String elasticSearchServer, String uri, boolean isDefaultIndexEnabled, boolean isDefaultTypeEnabled) {
        StringBuilder sb = new StringBuilder(elasticSearchServer);
        sb.append("/");
        if (isDefaultIndexEnabled && !uri.endsWith("bulk")) {
            if (isDefaultTypeEnabled) {
                sb.append(defaultIndex).append("/").append(defaultType).append(uri);
            } else {
                sb.append(defaultIndex).append(uri);
            }
        } else {
            sb.append(uri);
        }
        return sb.toString();
    }

    protected String modifyData(Object data, boolean isDefaultIndexEnabled, boolean isDefaultTypeEnabled) {
        String originalDataString = (String) data;
        if (isDefaultIndexEnabled) {
            originalDataString = originalDataString.replaceAll("<jesttempindex>", defaultIndex);
            if (isDefaultTypeEnabled) {
                originalDataString = originalDataString.replaceAll("<jesttemptype>", defaultType);
            }
        }
        return originalDataString;
    }

    public void removeDefaultIndex() {
        defaultIndex = null;
    }

    public void removeDefaultType() {
        defaultType = null;
    }

    public void registerDefaultIndex(String indexName) {
        defaultIndex = indexName;
    }

    public void registerDefaultType(String typeName) {
        defaultType = typeName;
    }

}
