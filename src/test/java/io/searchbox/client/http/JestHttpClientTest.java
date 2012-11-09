package io.searchbox.client.http;

import io.searchbox.client.http.apache.HttpGetWithEntity;
import org.apache.http.client.methods.*;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

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
        HttpUriRequest request = client.constructHttpMethod("GET", "jest/get", null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/get");
        assertTrue(request instanceof HttpGetWithEntity);
    }

    @Test
    public void constructPutHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("PUT", "jest/put", "data");
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/put");
        assertTrue(request instanceof HttpPut);
    }

    @Test
    public void constructPostHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("POST", "jest/post", "data");
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/post");
        assertTrue(request instanceof HttpPost);
    }

    @Test
    public void constructDeleteHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("DELETE", "jest/delete", null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/delete");
        assertTrue(request instanceof HttpDelete);
    }

    @Test
    public void constructHeadHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("HEAD", "jest/head", null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jest/head");
        assertTrue(request instanceof HttpHead);
    }
}
