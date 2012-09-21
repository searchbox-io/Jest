package io.searchbox.client.http;


import com.google.gson.*;
import io.searchbox.Action;
import io.searchbox.client.AbstractElasticSearchClient;
import io.searchbox.client.ElasticSearchClient;
import io.searchbox.client.ElasticSearchResult;
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
import org.elasticsearch.common.Unicode;

import java.io.IOException;


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
            Object data = clientRequest.getData();
            if (data != null) {
                httpPost.setEntity(new StringEntity(createJsonStringEntity(data), "UTF-8"));
            }
            response = httpClient.execute(httpPost);

        } else if (methodName.equalsIgnoreCase("PUT")) {
            HttpPut httpPut = new HttpPut(elasticSearchRestUrl);
            log.debug("PUT method created based on client request");
            Object data = clientRequest.getData();
            if (data != null) {
                httpPut.setEntity(new StringEntity(createJsonStringEntity(data), "UTF-8"));
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

        return deserializeResponse(response, clientRequest.getName(), clientRequest.getPathToResult());
    }

    private String createJsonStringEntity(Object data) {
        if (data instanceof byte[]) {
            return Unicode.fromBytes((byte[]) data);
        } else if (data instanceof String) {
            Object modifiedData = modifyData(data);
            if (isJson(modifiedData.toString())) {
                return modifiedData.toString();
            } else {
                return new Gson().toJson(modifiedData);
            }
        } else {
            return new Gson().toJson(data);
        }
    }

    private ElasticSearchResult deserializeResponse(HttpResponse response, String requestName, String pathToResult) throws IOException {
        return createNewElasticSearchResult(EntityUtils.toString(response.getEntity()), response.getStatusLine(), requestName, pathToResult);
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

    private boolean isJson(String data) {
        try {
            JsonElement result = new JsonParser().parse(data);
            return !result.equals(JsonNull.INSTANCE);
        } catch (JsonSyntaxException e) {
            //Check if this is a bulk request
            String[] bulkRequest = data.split("\n");
            return bulkRequest.length >= 1;
        }
    }
}
