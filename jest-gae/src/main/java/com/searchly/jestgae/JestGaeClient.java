package com.searchly.jestgae;

import com.google.appengine.api.urlfetch.*;
import com.google.gson.Gson;
import io.searchbox.action.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;

/**
 * @author Scopewriter
 */
public class JestGaeClient extends AbstractJestClient implements JestClient {

    private final static Logger log = LoggerFactory.getLogger(JestGaeClient.class);
    private final static String DEFAULT_OK_RESPONSE = "{\"ok\" : true, \"found\" : true}";
    private final static String DEFAULT_NOK_RESPONSE = "{\"ok\" : false, \"found\" : false}";

    protected final static String REQUEST_CONTENT_TYPE = "application/json; charset=utf-8";

    private URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
    private FetchOptions fetchOptions;

    /**
     * @throws java.io.IOException in case of a problem or the connection was aborted during request,
     *                     or in case of a problem while reading the response stream
     */
    @Override
    public <T extends JestResult> T execute(Action<T> clientRequest) throws IOException {
        HTTPRequest request = prepareRequest(clientRequest);
        HTTPResponse response = urlFetchService.fetch(request);
        return deserializeResponse(response, clientRequest, request.getMethod());
    }

    @Override
    public <T extends JestResult> void executeAsync(final Action<T> clientRequest, final JestResultHandler<T> resultHandler) {
        throw new UnsupportedOperationException("Jest-gae does not yet support async execution, sorry!");
    }

    @Override
    public void shutdownClient() {
        super.shutdownClient();
    }

    protected <T extends JestResult> HTTPRequest prepareRequest(final Action<T> clientRequest) throws MalformedURLException {
        String elasticSearchRestUrl = getRequestURL(getNextServer(), clientRequest.getURI());
        URL url = new URL(elasticSearchRestUrl);
        boolean setAuthorizationHeader = false;

        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            String urlWithoutCredentials = elasticSearchRestUrl.replace(userInfo + "@", "");
            url = new URL(urlWithoutCredentials);
            setAuthorizationHeader = true;
        }

        HTTPRequest request = constructHttpMethod(clientRequest.getRestMethodName(), url, clientRequest.getData(gson));
        log.debug("Request method={} url={}", clientRequest.getRestMethodName(), elasticSearchRestUrl);

        // add headers to request
        for (Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
            request.addHeader(new HTTPHeader(header.getKey(), header.getValue().toString()));
        }

        if (setAuthorizationHeader) {
            request.addHeader(new HTTPHeader("Authorization", "Basic " + Base64.encodeBase64String(userInfo.getBytes())));
        }

        return request;
    }

    protected HTTPRequest constructHttpMethod(String methodName, URL url, String payload) {
        HTTPRequest httpRequest = null;
        if (methodName.equalsIgnoreCase("POST")) {
            httpRequest = new HTTPRequest(url, HTTPMethod.POST, fetchOptions);
            log.debug("POST method created based on client request");
        } else if (methodName.equalsIgnoreCase("PUT")) {
            httpRequest = new HTTPRequest(url, HTTPMethod.PUT, fetchOptions);
            log.debug("PUT method created based on client request");
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            httpRequest = new HTTPRequest(url, HTTPMethod.DELETE, fetchOptions);
            log.debug("DELETE method created based on client request");
        } else if (methodName.equalsIgnoreCase("GET")) {
            httpRequest = new HTTPRequest(url, HTTPMethod.GET, fetchOptions);
            log.debug("GET method created based on client request");
        } else if (methodName.equalsIgnoreCase("HEAD")) {
            httpRequest = new HTTPRequest(url, HTTPMethod.HEAD, fetchOptions);
            log.debug("HEAD method created based on client request");
        }

        if (httpRequest != null && payload != null) {
            httpRequest.setPayload(payload.getBytes());
            httpRequest.setHeader(new HTTPHeader("Content-type", REQUEST_CONTENT_TYPE));
        }

        return httpRequest;
    }

    private <T extends JestResult> T deserializeResponse(HTTPResponse response, Action<T> clientRequest, HTTPMethod httpMethod) throws IOException {
        // If head method returns no content, content is added according to response code
        String responseBody = null;
        if (HTTPMethod.HEAD.equals(httpMethod)) {
            if (response.getContent() == null) {
                switch (response.getResponseCode()) {
                    case 200:
                        responseBody = DEFAULT_OK_RESPONSE;
                        break;
                    case 404:
                        responseBody = DEFAULT_NOK_RESPONSE;
                        break;
                }
            }
        } else {
            responseBody = response.getContent() == null ? null : new String(response.getContent(), "utf-8");
        }

        return clientRequest.createNewElasticSearchResult(
                responseBody,
                response.getResponseCode(),
                "", // No StatusMessage or ReasonPhrase in the GAE HTTPResponse
                gson
        );
    }

    public FetchOptions getFetchOptions() {
        return fetchOptions;
    }

    public void setFetchOptions(FetchOptions fetchOptions) {
        this.fetchOptions = fetchOptions;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

}
