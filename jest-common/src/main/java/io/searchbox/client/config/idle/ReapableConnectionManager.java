package io.searchbox.client.config.idle;

import java.util.concurrent.TimeUnit;

public interface ReapableConnectionManager {
    void closeIdleConnections(long idleTimeout, TimeUnit unit);
}
