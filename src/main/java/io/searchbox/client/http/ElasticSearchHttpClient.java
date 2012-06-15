package io.searchbox.client.http;


import io.searchbox.client.AbstractElasticSearchClient;
import io.searchbox.client.ElasticSearchClient;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.core.Action;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;


/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClient extends AbstractElasticSearchClient implements ElasticSearchClient {

    private static Logger log = Logger.getLogger(ElasticSearchHttpClient.class.getName());

    private HttpClient httpClient;

    private HttpAsyncClient asyncClient;

    public ElasticSearchResult execute(Action clientRequest) throws IOException {
        String elasticSearchRestUrl = getRequestURL(getElasticSearchServer(), clientRequest.getURI());
        String methodName = clientRequest.getRestMethodName();
        HttpResponse response = null;
        if (methodName.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost(elasticSearchRestUrl);
            log.debug("POST method created based on client request");
            if (clientRequest.getData() != null) {
                httpPost.setEntity(new StringEntity(clientRequest.getData().toString(), "UTF-8"));
            }
            response = httpClient.execute(httpPost);
        } else if (methodName.equalsIgnoreCase("PUT")) {
            HttpPut httpPut = new HttpPut(elasticSearchRestUrl);
            log.debug("PUT method created based on client request");
            if (clientRequest.getData() != null) {
                httpPut.setEntity(new StringEntity(clientRequest.getData().toString(), "UTF-8"));
            }
            response = httpClient.execute(httpPut);
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            HttpDelete httpDelete = new HttpDelete(elasticSearchRestUrl);
            log.debug("DELETE method created based on client request");
            response = httpClient.execute(httpDelete);
        } else if (methodName.equalsIgnoreCase("GET")) {
            HttpGet httpGet = new HttpGet(elasticSearchRestUrl);
            log.debug("GET method created based on client request");
            response = httpClient.execute(httpGet);
        }
        return deserializeResponse(response);
    }

    private ElasticSearchResult deserializeResponse(HttpResponse response) throws IOException {
        String jsonTxt = EntityUtils.toString(response.getEntity());
        Map json = convertJsonStringToMapObject(jsonTxt);
        return createNewElasticSearchResult(json, response.getStatusLine());
    }

    public <T> T executeAsync(Action clientRequest) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
