package io.searchbox.client.aws;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public class AWSSigningHttpRequestInterceptor implements HttpRequestInterceptor {
    private static final String AWS_ELASTICSEARCH_SERVICE="es";
    
    private final String awsRegion;
    private final String awsAccessKey;
    private final String awsSecretKey;
    
    public AWSSigningHttpRequestInterceptor(String awsRegion, String awsAccessKey, String awsSecretKey) {
        this.awsRegion = awsRegion;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if(request instanceof HttpUriRequest) {
            HttpUriRequest uriRequest=(HttpUriRequest) request;
            new AWSSignatureV4(AWS_ELASTICSEARCH_SERVICE, getAwsRegion(), getAwsAccessKey(), getAwsSecretKey()).sign(uriRequest);
        }
    }

    private String getAwsRegion() {
        return awsRegion;
    }

    private String getAwsAccessKey() {
        return awsAccessKey;
    }

    private String getAwsSecretKey() {
        return awsSecretKey;
    }
}
