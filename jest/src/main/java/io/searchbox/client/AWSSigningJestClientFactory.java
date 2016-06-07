package io.searchbox.client;

import io.searchbox.client.http.AWSSigningJestHttpClient;
import io.searchbox.client.http.JestHttpClient;

public class AWSSigningJestClientFactory extends JestClientFactory {
    private final String awsRegion;
    private final String awsAccessKey;
    private final String awsSecretKey;
    
    public AWSSigningJestClientFactory(String awsRegion, String awsAccessKey, String awsSecretKey) {
        this.awsRegion = awsRegion;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretKey = awsSecretKey;
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

    @Override
    protected JestHttpClient createClient() {
        return new AWSSigningJestHttpClient(getAwsAccessKey(), getAwsSecretKey(), getAwsRegion());
    }
}