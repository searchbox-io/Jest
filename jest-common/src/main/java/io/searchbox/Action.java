package io.searchbox;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData(Gson gson);

    String getPathToResult();

    Map<String, Object> getHeaders();

    Boolean isOperationSucceed(Map<String, ?> result);

    Boolean isOperationSucceed(JsonObject result);
}
