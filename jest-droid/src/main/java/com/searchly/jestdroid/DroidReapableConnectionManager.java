package com.searchly.jestdroid;

import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import io.searchbox.client.config.idle.ReapableConnectionManager;

import java.util.concurrent.TimeUnit;

public class DroidReapableConnectionManager implements ReapableConnectionManager {

    private final PoolingClientConnectionManager connectionManager;

    public DroidReapableConnectionManager(PoolingClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void closeIdleConnections(long idleTimeout, TimeUnit unit) {
        connectionManager.closeIdleConnections(idleTimeout, unit);
    }
}
