package com.searchly.jestdroid;

import io.searchbox.client.config.idle.ReapableConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class DroidReapableConnectionManager implements ReapableConnectionManager {

    private final PoolingHttpClientConnectionManager connectionManager;

    public DroidReapableConnectionManager(PoolingHttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void closeIdleConnections(long idleTimeout, TimeUnit unit) {
        connectionManager.closeIdleConnections(idleTimeout, unit);
    }
}
