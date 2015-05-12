package io.searchbox.action;

import com.google.gson.Gson;
import io.searchbox.client.JestResult;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
public interface Action<T extends JestResult> {

    String getURI();

    String getRestMethodName();

    String getData(Gson gson);

    String getPathToResult();

    Map<String, Object> getHeaders();

    T createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson);
}
