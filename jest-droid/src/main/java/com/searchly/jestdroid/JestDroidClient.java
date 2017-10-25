package com.searchly.jestdroid;

import com.google.gson.Gson;
import com.searchly.jestdroid.http.HttpDeleteWithEntity;
import com.searchly.jestdroid.http.HttpGetWithEntity;
import io.searchbox.action.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.client.config.exception.CouldNotConnectException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpHeadHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpPutHC4;
import org.apache.http.client.methods.HttpRequestBaseHC4;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtilsHC4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author cihat.keser
 */
public class JestDroidClient extends AbstractJestClient implements JestClient {

    private final static Logger log = LoggerFactory.getLogger(JestDroidClient.class);

    protected ContentType requestContentType = ContentType.APPLICATION_JSON.withCharset("utf-8");

    private HttpClient httpClient;

    @Override
    public <T extends JestResult> T execute(Action<T> clientRequest) throws IOException {
        return execute(clientRequest, null);
    }

    public <T extends JestResult> T execute(Action<T> clientRequest, RequestConfig requestConfig) throws IOException {
        String elasticSearchRestUrl = getRequestURL(getNextServer(), clientRequest.getURI(ElasticsearchVersion.UNKNOWN));
        HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData(gson), requestConfig);

        // add headers added to action
        if (!clientRequest.getHeaders().isEmpty()) {
            for (Map.Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
                request.addHeader(header.getKey(), header.getValue().toString());
            }
        }

        try {
            HttpResponse response = httpClient.execute(request);
            return deserializeResponse(response, clientRequest);
        } catch (HttpHostConnectException ex) {
            throw new CouldNotConnectException(ex.getHost().toURI(), ex);
        }
    }

    @Override
    public <T extends JestResult> void executeAsync(final Action<T> clientRequest, final JestResultHandler<? super T> resultHandler) {
        throw new UnsupportedOperationException("Jest-droid does not yet support async execution, sorry!");
    }

    protected HttpUriRequest constructHttpMethod(String methodName, String url, String payload, RequestConfig requestConfig) {
        HttpUriRequest httpUriRequest = null;

        if (methodName.equalsIgnoreCase("POST")) {
            httpUriRequest = new HttpPostHC4(url);
            log.debug("POST method created based on client request");
        } else if (methodName.equalsIgnoreCase("PUT")) {
            httpUriRequest = new HttpPutHC4(url);
            log.debug("PUT method created based on client request");
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            httpUriRequest = new HttpDeleteWithEntity(url);
            log.debug("DELETE method created based on client request");
        } else if (methodName.equalsIgnoreCase("GET")) {
            httpUriRequest = new HttpGetWithEntity(url);
            log.debug("GET method created based on client request");
        } else if (methodName.equalsIgnoreCase("HEAD")) {
            httpUriRequest = new HttpHeadHC4(url);
            log.debug("HEAD method created based on client request");
        }

        if (httpUriRequest instanceof HttpRequestBaseHC4 && requestConfig != null) {
            ((HttpRequestBaseHC4) httpUriRequest).setConfig(requestConfig);
        }

        if (httpUriRequest != null && httpUriRequest instanceof HttpEntityEnclosingRequest && payload != null) {
            EntityBuilder entityBuilder = EntityBuilder.create()
                    .setText(payload)
                    .setContentType(requestContentType);

            if (isRequestCompressionEnabled()) {
                entityBuilder.gzipCompress();
            }

            ((HttpEntityEnclosingRequest) httpUriRequest).setEntity(entityBuilder.build());
        }

        return httpUriRequest;
    }

    private <T extends JestResult> T deserializeResponse(HttpResponse response, Action<T> clientRequest) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        return clientRequest.createNewElasticSearchResult(
                response.getEntity() != null ? EntityUtilsHC4.toString(response.getEntity()) : null,
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase(),
                gson
        );
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

}
