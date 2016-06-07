package io.searchbox.client.http;

import org.apache.http.client.methods.HttpUriRequest;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.client.aws.AWSSignatureV4;

public class AWSSigningJestHttpClient extends JestHttpClient {
    private final String awsAccessKey;
    private final String awsSecretKey;
    private final String awsRegion;
    
    public AWSSigningJestHttpClient(String awsAccessKey, String awsSecretKey, String awsRegion) {
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
        this.awsRegion = awsRegion;
    }
    
    private String getAwsAccessKey() {
        return awsAccessKey;
    }

    private String getAwsSecretKey() {
        return awsSecretKey;
    }

    private String getAwsRegion() {
        return awsRegion;
    }
    
    private static final String AWS_ELASTICSEARCH_SERVICE="es";

    @Override
    protected <T extends JestResult> HttpUriRequest prepareRequest(Action<T> clientRequest) {
        HttpUriRequest request=super.prepareRequest(clientRequest);
        request = new AWSSignatureV4(AWS_ELASTICSEARCH_SERVICE, getAwsRegion(), getAwsAccessKey(), getAwsSecretKey()).sign(request);
        return request;
    }
}
