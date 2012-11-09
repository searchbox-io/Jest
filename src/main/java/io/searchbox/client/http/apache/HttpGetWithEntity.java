package io.searchbox.client.http.apache;

/**
 * @author ferhat sobay
 */

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";

    public HttpGetWithEntity() {
        super();
    }

    public HttpGetWithEntity(URI uri) {
        super();
        setURI(uri);
    }

    public HttpGetWithEntity(String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}