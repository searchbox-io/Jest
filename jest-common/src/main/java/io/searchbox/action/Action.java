package io.searchbox.action;

import com.google.gson.Gson;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ElasticsearchVersion;

import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
public interface Action<T extends JestResult> {

    String getRestMethodName();

    String getURI(ElasticsearchVersion elasticsearchVersion);

    String getData(Gson gson);

    String getPathToResult();

    Map<String, Object> getHeaders();

    T createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson);
}
