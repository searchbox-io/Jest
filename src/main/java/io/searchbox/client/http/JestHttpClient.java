package io.searchbox.client.http;


import com.google.gson.*;
import io.searchbox.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.common.Unicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


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

        HttpResponse response = httpClient.execute(request);

        return deserializeResponse(response, clientRequest.getName(), clientRequest.getPathToResult());

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

        asyncClient.execute(request, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(final HttpResponse response) {
                try {
                    JestResult jestResult = deserializeResponse(response, clientRequest.getName(), clientRequest.getPathToResult());
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
            return new HttpDelete(url);
        } else if (methodName.equalsIgnoreCase("GET")) {
            log.debug("GET method created based on client request");
            return new HttpGet(url);
        } else {
            return null;
        }
    }

    private String createJsonStringEntity(Object data) {
        if (data instanceof byte[]) {
            return Unicode.fromBytes((byte[]) data);
        } else if (data instanceof String) {
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

    private JestResult deserializeResponse(HttpResponse response, String requestName, String pathToResult) throws IOException {
        return createNewElasticSearchResult(EntityUtils.toString(response.getEntity()), response.getStatusLine(), requestName, pathToResult);
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
