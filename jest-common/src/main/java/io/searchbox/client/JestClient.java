package io.searchbox.client;


import io.searchbox.action.Action;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;


/**
 * @author Dogukan Sonmez
 */
public interface JestClient extends Closeable {

    <T extends JestResult> T execute(Action<T> clientRequest) throws IOException;

    <T extends JestResult> void executeAsync(Action<T> clientRequest, JestResultHandler<? super T> jestResultHandler);

    /**
     * @deprecated Use {@link #close()} instead.
     */
    @Deprecated
    void shutdownClient();

    void setServers(Set<String> servers);
}
