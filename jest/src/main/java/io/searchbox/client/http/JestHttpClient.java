package io.searchbox.client.http;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.searchbox.action.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.http.apache.HttpDeleteWithEntity;
import io.searchbox.client.http.apache.HttpGetWithEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;


/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class JestHttpClient extends AbstractJestClient implements JestClient {

    final static Logger log = LoggerFactory.getLogger(JestHttpClient.class);
    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncClient;
    private Charset entityEncoding = Charset.forName("utf-8");
    private HttpClientContext httpContext;

    @Override
    public <T extends JestResult> T execute(Action<T> clientRequest) throws IOException {

        String elasticSearchRestUrl = getRequestURL(getElasticSearchServer(), clientRequest.getURI());

        HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData(gson));

        // add headers added to action
        if (!clientRequest.getHeaders().isEmpty()) {
            for (Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
                request.addHeader(header.getKey(), header.getValue().toString());
            }
        }

        HttpResponse response = httpClient.execute(request, httpContext);

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

    @Override
    public <T extends JestResult> void executeAsync(final Action<T> clientRequest, final JestResultHandler<T> resultHandler)
            throws ExecutionException, InterruptedException, IOException {

        synchronized (this) {
            if (!asyncClient.isRunning()) {
                asyncClient.start();
            }
        }

        String elasticSearchRestUrl = getRequestURL(getElasticSearchServer(), clientRequest.getURI());

        final HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData(gson));

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
                    T jestResult = deserializeResponse(response, clientRequest);
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

    @Override
    public void shutdownClient() {
        super.shutdownClient();
        try {
            asyncClient.close();
            httpClient.close();
        } catch (Exception ex) {
            log.error("Exception occurred while shutting down the asynClient. Exception: " + ex.getMessage());
        }
    }

    protected HttpUriRequest constructHttpMethod(String methodName, String url, Object data) throws UnsupportedEncodingException {
        HttpUriRequest httpUriRequest = null;

        if (methodName.equalsIgnoreCase("POST")) {
            httpUriRequest = new HttpPost(url);
            log.debug("POST method created based on client request");
        } else if (methodName.equalsIgnoreCase("PUT")) {
            httpUriRequest = new HttpPut(url);
            log.debug("PUT method created based on client request");
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            httpUriRequest = new HttpDeleteWithEntity(url);
            log.debug("DELETE method created based on client request");
        } else if (methodName.equalsIgnoreCase("GET")) {
            httpUriRequest = new HttpGetWithEntity(url);
            log.debug("GET method created based on client request");
        } else if (methodName.equalsIgnoreCase("HEAD")) {
            httpUriRequest = new HttpHead(url);
            log.debug("HEAD method created based on client request");
        }

        if (httpUriRequest != null && httpUriRequest instanceof HttpEntityEnclosingRequestBase && data != null) {
            ((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(new StringEntity(createJsonStringEntity(data), entityEncoding));
        }

        return httpUriRequest;
    }

    private String createJsonStringEntity(Object data) {
        String entity;

        if (data instanceof String && isJson(data.toString())) {
            entity = data.toString();
        } else {
            entity = gson.toJson(data);
        }

        return entity;
    }

    private boolean isJson(String data) {
        try {
            JsonElement result = new JsonParser().parse(data);
            return !result.isJsonNull();
        } catch (JsonSyntaxException e) {
            //Check if this is a bulk request
            String[] bulkRequest = data.split("\n");
            return bulkRequest.length >= 1;
        }
    }

    private <T extends JestResult> T deserializeResponse(HttpResponse response, Action<T> clientRequest) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        return clientRequest.createNewElasticSearchResult(
                response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : null,
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase(),
                gson
        );
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CloseableHttpAsyncClient getAsyncClient() {
        return asyncClient;
    }

    public void setAsyncClient(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public Charset getEntityEncoding() {
        return entityEncoding;
    }

    public void setEntityEncoding(Charset entityEncoding) {
        this.entityEncoding = entityEncoding;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setHttpContext(HttpClientContext httpContext) {
        this.httpContext = httpContext;
    }

    public HttpClientContext getHttpContext() {
        return httpContext;
    }
}

