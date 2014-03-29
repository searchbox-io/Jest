package io.searchbox.client;


import io.searchbox.action.Action;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;


/**
 * @author Dogukan Sonmez
 */


public interface JestClient {

    <T extends JestResult> T execute(Action<T> clientRequest) throws Exception;

    <T extends JestResult> void executeAsync(Action<T> clientRequest, JestResultHandler<T> jestResultHandler)
            throws ExecutionException, InterruptedException, IOException;

    void shutdownClient();

    public void setServers(Set<String> servers);
}
