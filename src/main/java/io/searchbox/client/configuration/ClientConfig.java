package io.searchbox.client.configuration;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */


public class ClientConfig {


    private LinkedHashSet servers;


    public LinkedHashSet getServers() {
        return servers;
    }

    public void setServers(LinkedHashSet servers) {
        this.servers = servers;
    }


}
