package io.searchbox.client;


import com.google.gson.Gson;
import io.searchbox.Document;
import io.searchbox.core.Action;
import org.apache.http.StatusLine;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class AbstractElasticSearchClient implements ElasticSearchClient {

    private static Logger log = Logger.getLogger(AbstractElasticSearchClient.class.getName());

    public LinkedHashSet<String> servers;

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

    protected ElasticSearchResult createNewElasticSearchResult(Map json, StatusLine statusLine,String requestName) {
        ElasticSearchResult result = new ElasticSearchResult();
        result.setJsonMap(json);
        if ((statusLine.getStatusCode() / 100) == 2) {
            if(!isOperationSucceed(requestName)){
                result.setSucceeded(false);
                log.debug("http request was success but operation is failed Status code in 200");
            } else{
                result.setSucceeded(true);
                log.debug("Request and operation succeeded");
            }
        } else {
            result.setSucceeded(false);
            log.debug("Response is failed");
        }
        result.setDocuments(extractDocumentsFromResponse(json,requestName));

        return result;
    }

    private List<Document> extractDocumentsFromResponse(Map json, String requestName) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private boolean isOperationSucceed(String requestName) {
        return false;  //To change body of created methods use File | Settings | File Templates.
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
        return elasticSearchServer + "/" + uri;
    }

}
