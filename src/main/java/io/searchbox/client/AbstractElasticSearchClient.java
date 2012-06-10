package io.searchbox.client;


import io.searchbox.core.Action;


import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */


public class AbstractElasticSearchClient implements ElasticSearchClient{

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

    protected String convertIndexDataToJSON(Object data) {
        return data.toString();
    }

    protected String getElasticSearchServer() {
        for(String server:getServers()){
             return server;
        }
        throw new RuntimeException("No Server is assigned to client to connect");
    }
}
