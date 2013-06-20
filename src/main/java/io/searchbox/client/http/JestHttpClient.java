package io.searchbox.client.http;


import com.google.gson.*;
import io.searchbox.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.http.apache.HttpDeleteWithEntity;
import io.searchbox.client.http.apache.HttpGetWithEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;


/**
 * @author Dogukan Sonmez
 */


public class JestHttpClient extends AbstractJestClient implements JestClient {

    final static Logger log = LoggerFactory.getLogger(JestHttpClient.class);

    private HttpClient httpClient;

    private HttpAsyncClient asyncClient;

    public JestResult execute(Action clientRequest) throws IOException {

        String elasticSearchRestUrl = getRequestURL(getElasticSearchServer(), clientRequest.getURI());

        HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData());

        // add headers added to action
        if (!clientRequest.getHeaders().isEmpty()) {
            for (Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
                request.addHeader(header.getKey(), header.getValue() + "");
            }
        }

        HttpResponse response = httpClient.execute(request);

        // If head method returns no content, it is added according to response code thanks to https://github.com/hlassiege
        if (request.getMethod().equalsIgnoreCase("HEAD")) {
            if (response.getEntity() == null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    response.setEntity(new StringEntity("{\"ok\" : true, \"found\" : true}"));
                } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    response.setEntity(new StringEntity("{\"ok\" : false, \"found\" : false}"));
                }
            }
        }
        return deserializeResponse(response, clientRequest);
    }

    public void executeAsync(final Action clientRequest, final JestResultHandler<JestResult> resultHandler)
            throws ExecutionException, InterruptedException, IOException {

        synchronized (this) {
            if (asyncClient.getStatus() == IOReactorStatus.INACTIVE) {
                asyncClient.start();
            }
        }

        String elasticSearchRestUrl = getRequestURL(getElasticSearchServer(), clientRequest.getURI());

        final HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData());

        // add headers added to action
        if (!clientRequest.getHeaders().isEmpty()) {
            for (Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
                request.addHeader(header.getKey(), header.getValue() + "");
            }
        }

        asyncClient.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse response) {
                try {
                    JestResult jestResult = deserializeResponse(response, clientRequest);
                    resultHandler.completed(jestResult);
                } catch (IOException e) {
                    log.error("Exception occurred while serializing the response. Exception: " + e.getMessage());
                }
            }

            @Override
            public void failed(final Exception ex) {
                resultHandler.failed(ex);
            }

            @Override
            public void cancelled() {
            }
        });

    }

    public void shutdownClient() {
        super.shutdownClient();
        try {
            asyncClient.shutdown();
        } catch (Exception ex) {
            log.error("Exception occurred while shutting down the asynClient. Exception: " + ex.getMessage());
        }
    }

    protected HttpUriRequest constructHttpMethod(String methodName, String url, Object data) throws UnsupportedEncodingException {
        if (methodName.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost(url);
            log.debug("POST method created based on client request");
            if (data != null) httpPost.setEntity(new StringEntity(createJsonStringEntity(data), "UTF-8"));
            return httpPost;
        } else if (methodName.equalsIgnoreCase("PUT")) {
            HttpPut httpPut = new HttpPut(url);
            log.debug("PUT method created based on client request");
            if (data != null) httpPut.setEntity(new StringEntity(createJsonStringEntity(data), "UTF-8"));
            return httpPut;
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            log.debug("DELETE method created based on client request");
            HttpDeleteWithEntity httpDelete = new HttpDeleteWithEntity(url);
            if (data != null) httpDelete.setEntity(new StringEntity(createJsonStringEntity(data), "UTF-8"));
            return httpDelete;
        } else if (methodName.equalsIgnoreCase("GET")) {
            log.debug("GET method created based on client request");
            HttpGetWithEntity httpGet = new HttpGetWithEntity(url);
            if (data != null) httpGet.setEntity(new StringEntity(createJsonStringEntity(data), "UTF-8"));
            return httpGet;
        } else if (methodName.equalsIgnoreCase("HEAD")) {
            log.debug("HEAD method created based on client request");
            return new HttpHead(url);
        } else {
            return null;
        }
    }

    private String createJsonStringEntity(Object data) {
        if (data instanceof String) {
            if (isJson(data.toString())) {
                return data.toString();
            } else {
                return new Gson().toJson(data);
            }
        } else {
            return new Gson().toJson(data);
        }
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

    private JestResult deserializeResponse(HttpResponse response, Action clientRequest) throws IOException {
        return createNewElasticSearchResult(
                response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : null,
                response.getStatusLine(),
                clientRequest);
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
