package io.searchbox.client;

import io.searchbox.client.configuration.ClientConfig;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Dogukan Sonmez
 */


@Component
public class ElasticSearchClientFactoryImpl {

    @Bean
    public ElasticSearchClient createDefaultHttpClient() {
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(100);
        HttpClient httpclient = new DefaultHttpClient(cm);
        ElasticSearchHttpClient client = new ElasticSearchHttpClient();
        client.setHttpClient(httpclient);
        return client;
    }

    @Bean
    public ElasticSearchClient createHttpClient(ClientConfig clientConfig) {
        return null;
    }

    @Bean
    public ElasticSearchClient createAsyncHttpClient(ClientConfig clientConfig) {
        return null;
    }


    @Bean
    public ElasticSearchClient createThriftClient(ClientConfig clientConfig) {
        return null;
    }
}
