package io.searchbox.client.http;

import io.searchbox.client.AbstractElasticSearchClient;
import io.searchbox.client.ElasticSearchClient;
import org.apache.http.nio.client.HttpAsyncClient;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchAsyncHttpClient extends AbstractElasticSearchClient implements ElasticSearchClient {

    private HttpAsyncClient asyncClient;

    public HttpAsyncClient getAsyncClient() {
        return asyncClient;
    }

    public void setAsyncClient(HttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }
}
