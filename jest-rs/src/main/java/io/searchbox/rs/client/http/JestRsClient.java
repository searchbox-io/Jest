package io.searchbox.rs.client.http;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import io.searchbox.action.Action;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Explain;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class JestRsClient extends AbstractJestClient implements JestClient {

    private final static Logger log = Logger.getLogger(JestRsClient.class.getName());
    // private final static HttpEntity DEFAULT_OK_RESPONSE = EntityBuilder.create().setText("{\"ok\" : true, \"found\" : true}").build();
    // private final static HttpEntity DEFAULT_NOK_RESPONSE = EntityBuilder.create().setText("{\"ok\" : false, \"found\" : false}").build();


    private Client httpClient;

    /**
     * @throws IOException in case of a problem or the connection was aborted during request,
     *                     or in case of a problem while reading the response stream
     */
    @Override
    public <T extends JestResult> T execute(Action<T> clientRequest) throws IOException {
        Invocation response = prepareRequest(clientRequest);

        return deserializeResponse(response.invoke(), clientRequest);
    }

    @Override
    public <T extends JestResult> void executeAsync(final Action<T> clientRequest, final JestResultHandler<? super T> resultHandler) {

        Invocation response = prepareRequest(clientRequest);
        response.submit(new DefaultCallback<T>(clientRequest, resultHandler));
    }

    @Override
    public void shutdownClient() {
        super.shutdownClient();
     
        httpClient.close();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note: JAX-RS correctly disallows GET with message body. However some of
     * the methods use GetWithEntity before such as {@link Explain} which also allow
     * POST.  For those cases, GET is converted to POST.
     * </p>
     * 
     * @param clientRequest
     *            Client request
     * @return JAX-RS invocation.
     */
    protected <T extends JestResult> Invocation prepareRequest(final Action<T> clientRequest) {
        String elasticSearchRestUrl = getRequestURL(getNextServer(), clientRequest.getURI());

        log.finest(MessageFormat.format("Request method={0} url={1}", clientRequest.getRestMethodName(), elasticSearchRestUrl));

        Builder requestBuilder = httpClient.target(elasticSearchRestUrl).request(MediaType.APPLICATION_JSON);
        // add headers added to action
        for (Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue().toString());
        }

        String data = clientRequest.getData(gson);
        if (data == null || data.isEmpty()) {
            return requestBuilder.build(clientRequest.getRestMethodName());
        } else {
            if ("GET".equals(clientRequest.getRestMethodName())) {
                return requestBuilder.buildPost(Entity.entity(data, MediaType.APPLICATION_JSON_TYPE));
            } else {
                return requestBuilder.build(clientRequest.getRestMethodName(), Entity.entity(data, MediaType.APPLICATION_JSON_TYPE));
            }
        }
    }

    private <T extends JestResult> T deserializeResponse(Response response, Action<T> clientRequest) throws IOException {
        return clientRequest.createNewElasticSearchResult(
                response.getEntity() == null ? null : response.readEntity(String.class),
                response.getStatus(),
                response.getStatusInfo().getReasonPhrase(),
                gson
        );
    }

    public Client getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(Client httpClient) {
        this.httpClient = httpClient;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    protected class DefaultCallback<T extends JestResult> implements InvocationCallback<Response> {
        private final Action<T> clientRequest;
        private final JestResultHandler<? super T> resultHandler;

        public DefaultCallback(Action<T> clientRequest, JestResultHandler<? super T> resultHandler) {
            this.clientRequest = clientRequest;
            this.resultHandler = resultHandler;
        }

        @Override
        public void completed(final Response response) {
            T jestResult = null;
            try {
                jestResult = deserializeResponse(response, clientRequest);
            } catch (IOException e) {
                failed(e);
            }
            if (jestResult != null) resultHandler.completed(jestResult);
        }

        @Override
        public void failed(final Throwable ex) {
            log.log(Level.SEVERE, "Exception occurred during async execution.", ex);
            // JEST-COMMON should really accept throwable not just exception
            resultHandler.failed((Exception)ex);
        }

    }

}
