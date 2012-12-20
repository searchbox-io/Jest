package io.searchbox;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData();

    String getName();

    String getPathToResult();
    
    Map<String, Object> getHeaders();

}
