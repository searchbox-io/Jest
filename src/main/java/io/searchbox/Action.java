package io.searchbox;

/**
 * @author Dogukan Sonmez
 */


public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData();

    String getName();

    boolean isDefaultIndexEnabled();

    boolean isDefaultTypeEnabled();

    boolean isBulkOperation();
}
