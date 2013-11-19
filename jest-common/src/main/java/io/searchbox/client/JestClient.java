package io.searchbox.client;


import io.searchbox.Action;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;


/**
 * @author Dogukan Sonmez
 */


public interface JestClient {

    JestResult execute(Action clientRequest) throws Exception;

    void executeAsync(Action clientRequest, JestResultHandler<JestResult> jestResultHandler)
            throws ExecutionException, InterruptedException, IOException;

    void shutdownClient();

    public void setServers(Set<String> servers);
}
