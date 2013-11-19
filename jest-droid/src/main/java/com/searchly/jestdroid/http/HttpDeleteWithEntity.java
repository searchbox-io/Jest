package com.searchly.jestdroid.http;

import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * @author ferhat
 */
public class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {

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