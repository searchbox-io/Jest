package io.searchbox.client.http;


import io.searchbox.Document;
import io.searchbox.client.AbstractElasticSearchClient;
import io.searchbox.client.ElasticSearchClient;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.core.Action;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClient extends AbstractElasticSearchClient implements ElasticSearchClient {

    private HttpClient httpClient;

    private HttpAsyncClient asyncClient;


    public ElasticSearchResult execute(Action clientRequest) throws IOException {
        String elasticSearchRestUrl = getElasticSearchServer() + "/" + clientRequest.getURI();
        String methodName = clientRequest.getRestMethodName();
        HttpResponse response = null;
        if (methodName.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost(elasticSearchRestUrl);
            if (clientRequest.getData() != null) {
                httpPost.setEntity(new StringEntity(clientRequest.getData().toString(), "UTF-8"));
            }
            response = httpClient.execute(httpPost);
        } else if (methodName.equalsIgnoreCase("PUT")) {
            HttpPut httpPut = new HttpPut(elasticSearchRestUrl);
            if (clientRequest.getData() != null) {
                httpPut.setEntity(new StringEntity(clientRequest.getData().toString(), "UTF-8"));
            }
            response = httpClient.execute(httpPut);
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            HttpDelete httpDelete = new HttpDelete(elasticSearchRestUrl);
            response = httpClient.execute(httpDelete);
        } else if (methodName.equalsIgnoreCase("GET")) {
            HttpGet httpGet = new HttpGet(elasticSearchRestUrl);
            response = httpClient.execute(httpGet);
        }
        return deserializeResponse(response);
    }

    private ElasticSearchResult deserializeResponse(HttpResponse response) {
        String jsonTxt = null;
        try {
            jsonTxt = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
        ElasticSearchResult result = new ElasticSearchResult();
        result.setJson(json);
        if ((response.getStatusLine().getStatusCode()/100) == 2) {
            result.setSucceeded(true);
        } else {
            result.setSucceeded(false);
        }
        return result;
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
