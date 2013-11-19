package io.searchbox.client.config.exception;

/**
 * Exception that specified that the client has no
 * knowledge of an elasticsearch node to communicate with.
 *
 */
public class NoServerConfiguredException  extends RuntimeException {

    static final long serialVersionUID = -7034897190745766912L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public NoServerConfiguredException(String message) {
        super(message);
    }

}
