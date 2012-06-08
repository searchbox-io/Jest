package io.searchbox.core;

/**
 * @author Dogukan Sonmez
 */


public interface ClientRequest {

    String getURI();

    String getRestMethodName();

    Object getData();
}
