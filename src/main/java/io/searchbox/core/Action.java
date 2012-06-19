package io.searchbox.core;

/**
 * @author Dogukan Sonmez
 */


public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData();

    String getName();
}
