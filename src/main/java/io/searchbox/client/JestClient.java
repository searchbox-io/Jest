package io.searchbox.client;


import io.searchbox.Action;

import java.io.IOException;


/**
 * @author Dogukan Sonmez
 */


public interface JestClient {

    SearchResult execute(Action clientRequest) throws IOException;

    <T> T executeAsync(Action clientRequest);

    void shutdownClient();

    void registerDefaultIndex(String indexName);

    void registerDefaultType(String typeName);

    void removeDefaultIndex();

    void removeDefaultType();

}
