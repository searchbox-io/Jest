package io.searchbox.client;

import io.searchbox.indices.Index;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */


public class AbstractElasticSearchClient implements ElasticSearchClient{

    public LinkedHashSet servers;

    public LinkedHashSet getServers() {
        return servers;
    }

    public void setServers(LinkedHashSet servers) {
        this.servers = servers;
    }

    public void index(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void index(String name, String type, String id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void indexAsync(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteAsync(Index index) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object get(String name, String type, String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
}
