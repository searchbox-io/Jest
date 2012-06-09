package io.searchbox.client;


import io.searchbox.core.Action;

import java.io.IOException;


/**
 * @author Dogukan Sonmez
 */


public interface ElasticSearchClient {

    <T> T execute(Action clientRequest) throws IOException;

    <T> T executeAsync(Action clientRequest);

    void shutdownClient();

}
