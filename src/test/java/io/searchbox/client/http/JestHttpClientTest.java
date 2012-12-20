package io.searchbox.client.http;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.apache.HttpGetWithEntity;
import io.searchbox.core.Search;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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
		assertTrue(request instanceof HttpDelete);
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
					}});
				
				resp.setEntity(new AbstractHttpEntity () {

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
						return new ByteArrayInputStream("ok".getBytes());
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

		ClientConfig clientConfig = new ClientConfig();
		LinkedHashSet<String> servers = new LinkedHashSet<String>();
		servers.add("http://localhost:9200");
		clientConfig.getServerProperties().put(ClientConstants.SERVER_LIST,
				servers);

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		factory.setClientConfig(clientConfig);
		JestHttpClient clientWithMockedHttpClient = (JestHttpClient) factory
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
		
		Search search = new Search(query);
		// multiple index or types can be added.
		search.addIndex("twitter");
		search.addType("tweet");
		// add a header
		search.addHeader("foo", "bar");
		// send request (not really)
		clientWithMockedHttpClient.execute(search);
		// gather saved request
		HttpUriRequest savedReq = ((HttpClientMock) clientWithMockedHttpClient
				.getHttpClient()).savedRequest;

		assertNotNull(savedReq.getFirstHeader("foo"));
	}
}
