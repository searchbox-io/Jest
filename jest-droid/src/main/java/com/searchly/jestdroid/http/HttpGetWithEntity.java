package com.searchly.jestdroid.http;

/**
 * @author ferhat sobay
 */

import org.apache.http.client.methods.HttpEntityEnclosingRequestBaseHC4;
import org.apache.http.client.methods.HttpGet;

import java.net.URI;

public class HttpGetWithEntity extends HttpEntityEnclosingRequestBaseHC4 {

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
        return HttpGet.METHOD_NAME;
    }
}