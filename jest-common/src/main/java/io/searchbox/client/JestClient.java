package io.searchbox.client;


import io.searchbox.action.Action;

import java.io.IOException;
import java.util.Set;


/**
 * @author Dogukan Sonmez
 */
public interface JestClient {

    <T extends JestResult> T execute(Action<T> clientRequest) throws IOException;

    <T extends JestResult> void executeAsync(Action<T> clientRequest, JestResultHandler<? super T> jestResultHandler);

    void shutdownClient();

    void setServers(Set<String> servers);
}
