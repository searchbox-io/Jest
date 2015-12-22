package io.searchbox.client.config.idle;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.searchbox.client.config.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Used to reap idle connections from the connection manager.
 */
public class IdleConnectionReaper extends AbstractScheduledService {

    final static Logger logger = LoggerFactory.getLogger(IdleConnectionReaper.class);

    private final ReapableConnectionManager reapableConnectionManager;
    private final ClientConfig clientConfig;

    public IdleConnectionReaper(ClientConfig clientConfig, ReapableConnectionManager reapableConnectionManager) {
        this.reapableConnectionManager = reapableConnectionManager;
        this.clientConfig = clientConfig;
    }

    @Override
    protected void runOneIteration() throws Exception {
        logger.debug("closing idle connections...");
        reapableConnectionManager.closeIdleConnections(clientConfig.getMaxConnectionIdleTime(),
                                                       clientConfig.getMaxConnectionIdleTimeDurationTimeUnit());
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0l,
                                               clientConfig.getMaxConnectionIdleTime(),
                                               clientConfig.getMaxConnectionIdleTimeDurationTimeUnit());
    }

    @Override
    protected ScheduledExecutorService executor() {
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(serviceName())
                .build());
        // Add a listener to shutdown the executor after the service is stopped.  This ensures that the
        // JVM shutdown will not be prevented from exiting after this service has stopped or failed.
        // Technically this listener is added after start() was called so it is a little gross, but it
        // is called within doStart() so we know that the service cannot terminate or fail concurrently
        // with adding this listener so it is impossible to miss an event that we are interested in.
        addListener(new Listener() {
            @Override public void terminated(State from) {
                executor.shutdown();
            }
            @Override public void failed(State from, Throwable failure) {
                executor.shutdown();
            }}, MoreExecutors.directExecutor());
        return executor;
    }
}
