package com.searchly.jestdroid.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBaseHC4;

import java.net.URI;

/**
 * @author ferhat
 */
public class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBaseHC4 {

    public HttpDeleteWithEntity() {
        super();
    }

    public HttpDeleteWithEntity(URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithEntity(String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return HttpDelete.METHOD_NAME;
    }
}