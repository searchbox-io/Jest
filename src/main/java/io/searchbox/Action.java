package io.searchbox;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData();

    String getPathToResult();

    Map<String, Object> getHeaders();

    Boolean isOperationSucceed(Map result);

}
