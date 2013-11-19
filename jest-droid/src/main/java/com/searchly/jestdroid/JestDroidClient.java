package com.searchly.jestdroid;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.*;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.searchly.jestdroid.http.HttpDeleteWithEntity;
import com.searchly.jestdroid.http.HttpGetWithEntity;
import io.searchbox.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author cihat.keser
 */
public class JestDroidClient extends AbstractJestClient implements JestClient {

    final static Logger log = LoggerFactory.getLogger(JestDroidClient.class);
    private HttpClient httpClient;
    private Charset entityEncoding = Charset.forName("utf-8");

    @Override
    public JestResult execute(Action clientRequest) throws IOException {

        String elasticSearchRestUrl = getRequestURL(getElasticSearchServer(), clientRequest.getURI());

        HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData(gson));

        // add headers added to action
        if (!clientRequest.getHeaders().isEmpty()) {
            for (Map.Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
                request.addHeader(header.getKey(), header.getValue().toString());
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

    @Override
    public void executeAsync(final Action clientRequest, final JestResultHandler<JestResult> resultHandler)
            throws ExecutionException, InterruptedException, IOException {
        throw new UnsupportedOperationException("Jest-droid does not yet support async execution, sorry!");
    }

    public void shutdownClient() {
        super.shutdownClient();
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

    private JestResult deserializeResponse(HttpResponse response, Action clientRequest) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        return createNewElasticSearchResult(
                response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : null,
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase(),
                clientRequest);
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
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

}
