package io.searchbox.client.http;

import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import org.elasticsearch.action.*;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.common.io.stream.BytesStreamInput;
import org.elasticsearch.threadpool.ThreadPool;

import java.io.IOException;
import java.util.Map;

/*
* This client is an adapter between ES native Java API and Jest
 */

public class HttpClient extends AbstractClient {

    private JestHttpClient httpClient;

    public HttpClient(JestHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public ThreadPool threadPool() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public AdminClient admin() {
        return null;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> ActionFuture<Response> execute(Action<Request, Response, RequestBuilder> action, Request request) {

        PlainActionFuture<Response> future = PlainActionFuture.newFuture();

        io.searchbox.Action restAction;
        try {

            if (request instanceof IndexRequest) {
                restAction = new Index(request);
            } else if (request instanceof SearchRequest) {
                restAction = new Search(request);
            } else if (request instanceof UpdateRequest) {
                restAction = new Update(request);
            } else if (request instanceof DeleteRequest) {
                restAction = new Delete(request);
            } else if (request instanceof CountRequest) {
                restAction = new Count(request);
            } else if (request instanceof GetRequest) {
                restAction = new Get(request);
            } else {
                throw new RuntimeException("Given request" + request.toString() + " is not supported by JEST");
            }

            JestResult result;

            result = httpClient.execute(restAction);
            Map jsonMap = result.getJsonMap();
            Response response = action.newResponse();
            response.readFrom(new BytesStreamInput(restAction.createByteResult(jsonMap), true));
            future.onResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
            future.onFailure(e);
        }

        return future;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> void execute(Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {

        io.searchbox.Action restAction;

        try {

            if (request instanceof IndexRequest) {
                restAction = new Index(request);
            } else if (request instanceof SearchRequest) {
                restAction = new Search(request);
            } else if (request instanceof UpdateRequest) {
                restAction = new Update(request);
            } else if (request instanceof DeleteRequest) {
                restAction = new Delete(request);
            } else if (request instanceof CountRequest) {
                restAction = new Count(request);
            } else if (request instanceof GetRequest) {
                restAction = new Get(request);
            } else if (request instanceof PercolateRequest) {
                restAction = new Percolate(request);
            } else {
                throw new RuntimeException("Given request" + request.toString() + " is not supported by JEST");
            }

            JestResult result = httpClient.execute(restAction);
            Map jsonMap = result.getJsonMap();
            Response response = action.newResponse();
            response.readFrom(new BytesStreamInput(restAction.createByteResult(jsonMap), true));
            listener.onResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(e);
        }

    }
}
