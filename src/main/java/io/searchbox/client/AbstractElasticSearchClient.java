package io.searchbox.client;

import io.searchbox.indices.Index;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    public void index(Index index) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void indexAsync(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(Index index) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteAsync(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object get(String name, String type, String id) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update(Index index) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateAsync(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object search() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void percolate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void bulk() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void count() {
        //To change body of implemented methods use File | Settings | File Templates.
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

    protected String buildRestUrl(Index index, String server) {
        StringBuilder sb = new StringBuilder();
        sb.append(server)
                .append("/")
                .append(index.getName())
                .append("/")
                .append(index.getType())
                .append("/")
                .append(index.getId());
        return sb.toString();
    }
}
