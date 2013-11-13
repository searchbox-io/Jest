package io.searchbox.client.http;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.apache.HttpDeleteWithEntity;
import io.searchbox.client.http.apache.HttpGetWithEntity;
import io.searchbox.core.Search;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import java.io.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */
public class JestHttpClientTest {

    JestHttpClient client = new JestHttpClient();

    @Test
    public void constructGetHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("GET", "jest/get",
                null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/get");
        assertTrue(request instanceof HttpGetWithEntity);
    }

    @Test
    public void constructPutHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("PUT", "jest/put",
                "data");
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/put");
        assertTrue(request instanceof HttpPut);
    }

    @Test
    public void constructPostHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("POST",
                "jest/post", "data");
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/post");
        assertTrue(request instanceof HttpPost);
    }

    @Test
    public void constructDeleteHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("DELETE",
                "jest/delete", null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/delete");
        assertTrue(request instanceof HttpDeleteWithEntity);
    }

    @Test
    public void constructHeadHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("HEAD",
                "jest/head", null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/head");
        assertTrue(request instanceof HttpHead);
    }

    @Test
    public void addHeadersToRequest() throws IOException {
        JestHttpClient clientWithMockedHttpClient;
        class HttpClientMock implements HttpClient {

            // so we can inspect it
            public HttpUriRequest savedRequest;

            @Override
            public HttpResponse execute(HttpUriRequest request)
                    throws IOException, ClientProtocolException {
                // save request
                savedRequest = request;
                HttpResponse resp = new BasicHttpResponse(new StatusLine() {
                    @Override
                    public int getStatusCode() {
                        // TODO Auto-generated method stub
                        return 200;
                    }

                    @Override
                    public ProtocolVersion getProtocolVersion() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String getReasonPhrase() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });

                resp.setEntity(new AbstractHttpEntity() {

                    @Override
                    public boolean isRepeatable() {
                        // TODO Auto-generated method stub
                        return true;
                    }

                    @Override
                    public long getContentLength() {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public InputStream getContent() throws IOException,
                            IllegalStateException {
                        return new ByteArrayInputStream("{ok: true, exists: true}".getBytes());
                    }

                    @Override
                    public void writeTo(OutputStream outstream)
                            throws IOException {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean isStreaming() {
                        // TODO Auto-generated method stub
                        return false;
                    }


                });
                return resp;
            }

            @Override
            public HttpParams getParams() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ClientConnectionManager getConnectionManager() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HttpResponse execute(HttpUriRequest request,
                                        HttpContext context) throws IOException,
                    ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HttpResponse execute(HttpHost target, HttpRequest request)
                    throws IOException, ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HttpResponse execute(HttpHost target, HttpRequest request,
                                        HttpContext context) throws IOException,
                    ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T execute(HttpUriRequest request,
                                 ResponseHandler<? extends T> responseHandler)
                    throws IOException, ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T execute(HttpUriRequest request,
                                 ResponseHandler<? extends T> responseHandler,
                                 HttpContext context) throws IOException,
                    ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T execute(HttpHost target, HttpRequest request,
                                 ResponseHandler<? extends T> responseHandler)
                    throws IOException, ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> T execute(HttpHost target, HttpRequest request,
                                 ResponseHandler<? extends T> responseHandler,
                                 HttpContext context) throws IOException,
                    ClientProtocolException {
                // TODO Auto-generated method stub
                return null;
            }

        }

        HttpClientConfig httpClientConfig = new HttpClientConfig.Builder("http://localhost:9200").build();

        // Construct a new Jest client according to configuration via factory
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(httpClientConfig);
        clientWithMockedHttpClient = (JestHttpClient) factory
                .getObject();

        clientWithMockedHttpClient.setHttpClient(new HttpClientMock());

        // could reuse the above setup for testing core types against expected
        // HttpUriRequest (more of an end to end test)

        String query = "{\n" +
                "    \"query\": {\n" +
                "        \"filtered\" : {\n" +
                "            \"query\" : {\n" +
                "                \"query_string\" : {\n" +
                "                    \"query\" : \"test\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"filter\" : {\n" +
                "                \"term\" : { \"user\" : \"kimchy\" }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        Search search = new Search.Builder(query)
                // multiple index or types can be added.
                .addIndex("twitter")
                .addType("tweet")
                .setHeader("foo", "bar")
                .build();
        // send request (not really)
        clientWithMockedHttpClient.execute(search);
        // gather saved request
        HttpUriRequest savedReq = ((HttpClientMock) clientWithMockedHttpClient
                .getHttpClient()).savedRequest;

        assertNotNull(savedReq.getFirstHeader("foo"));
    }
}
