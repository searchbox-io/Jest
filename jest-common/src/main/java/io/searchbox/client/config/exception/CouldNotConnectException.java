package io.searchbox.client.config.exception;

import java.io.IOException;

/**
 * Wrapper for a the host address of an HTTPConnectException
 * @author Brian Harrington
 */
public class CouldNotConnectException extends IOException {

    private final String host;

    public CouldNotConnectException(String host, Throwable cause) {
        super("Could not connect to " + host, cause);
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
