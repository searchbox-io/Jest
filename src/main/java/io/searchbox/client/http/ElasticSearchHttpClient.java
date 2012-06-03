package io.searchbox.client.http;

import io.searchbox.client.AbstractElasticSearchClient;
import io.searchbox.client.ElasticSearchClient;
import io.searchbox.indices.Index;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.nio.client.HttpAsyncClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClient extends AbstractElasticSearchClient implements ElasticSearchClient {

    private HttpClient httpClient;

    private HttpAsyncClient asyncClient;

    public void index(Index index) throws Exception {
        String elasticSearchRestUrl = buildRestUrl(index, getElasticSearchServer());
        HttpPut httpPut = new HttpPut(elasticSearchRestUrl);
        String json = convertIndexDataToJSON(index.getData());
        httpPut.setEntity(new StringEntity(json, "UTF-8"));
        httpClient.execute(httpPut);
    }

    public void delete(Index index) throws IOException {
        String elasticSearchRestUrl = buildRestUrl(index, getElasticSearchServer());
        HttpDelete httpDelete = new HttpDelete(elasticSearchRestUrl);
        httpClient.execute(httpDelete);
    }

    public Object get(String name, String type, String id) throws IOException {
        String elasticSearchRestUrl = buildRestUrl(new Index(name, type, id), getElasticSearchServer());
        HttpGet httpget = new HttpGet(elasticSearchRestUrl);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        return httpClient.execute(httpget, responseHandler);
    }

    public void update(Index index) throws IOException {
        String elasticSearchRestUrl = buildRestUrl(index, getElasticSearchServer());
        HttpPost httpPost = new HttpPost(elasticSearchRestUrl);
        String json = convertIndexDataToJSON(index.getData());
        httpPost.setEntity(new StringEntity(json, "UTF-8"));
        httpClient.execute(httpPost);
    }


    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpAsyncClient getAsyncClient() {
        return asyncClient;
    }

    public void setAsyncClient(HttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }
}
