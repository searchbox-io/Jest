package io.searchbox.client;


import io.searchbox.core.ClientRequest;

import java.io.IOException;


/**
 * @author Dogukan Sonmez
 */


public interface ElasticSearchClient {

    <T> T execute(ClientRequest clientRequest) throws IOException;

    <T> T executeAsync(ClientRequest clientRequest);

    void shutdownClient();

}
