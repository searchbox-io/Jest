package io.searchbox.client.http;

import io.searchbox.client.AbstractElasticSearchClient;
import io.searchbox.client.ElasticSearchClient;
import org.apache.http.client.HttpClient;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClient extends AbstractElasticSearchClient implements ElasticSearchClient {

    private HttpClient httpClient;

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
