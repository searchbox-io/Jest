package io.searchbox.action;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
public interface Action<T extends JestResult> {

    String getURI();

    String getRestMethodName();

    Object getData(Gson gson);

    String getPathToResult();

    Map<String, Object> getHeaders();

    Boolean isOperationSucceed(Map<String, ?> result);

    Boolean isOperationSucceed(JsonObject result);

    T createNewElasticSearchResult(String json, int statusCode, String reasonPhrase, Gson gson);
}
