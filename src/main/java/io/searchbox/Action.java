package io.searchbox;

import java.util.Map;

import com.google.gson.JsonObject;

/**
 * @author Dogukan Sonmez
 */


public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData();

    String getPathToResult();

    Map<String, Object> getHeaders();

    Boolean isOperationSucceed(Map<String,?> result);

    Boolean isOperationSucceed(JsonObject result);
}
