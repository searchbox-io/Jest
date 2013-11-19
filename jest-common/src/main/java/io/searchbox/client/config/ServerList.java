package io.searchbox.client.config;

import java.util.Set;

/**
 * Provides the interface to iterating over a set of
 * server addresses.
 */
public interface ServerList {

    /**
     * Returns the "next" server the client should talk to.  The next item is
     * determined by the implementation.
     * @return
     */
    public String getServer();

    /**
     * Returns the set of servers from which the ServerList implementation
     * was generated.  This method is only here to satisfy unit test
     * @return
     */
    public Set getServers();

}
