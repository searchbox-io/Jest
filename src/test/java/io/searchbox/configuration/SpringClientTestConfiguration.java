package io.searchbox.configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;

/**
 * @author Dogukan Sonmez
 */

@Configuration
public class SpringClientTestConfiguration {

    public
    @Bean
    ClientConfig clientConfig() {
        ClientConfig clientConfig = new ClientConfig();
        LinkedHashSet<String> servers = new LinkedHashSet<String>();
        servers.add("http://localhost:9200");
        clientConfig.getServerProperties().put(ClientConstants.SERVER_LIST, servers);
        clientConfig.getClientFeatures().put(ClientConstants.IS_MULTI_THREADED, true);
        clientConfig.getClientFeatures().put(ClientConstants.DEFAULT_INDEX, "cvbank");
        clientConfig.getClientFeatures().put(ClientConstants.DEFAULT_TYPE, "candidate");
        return clientConfig;
    }

    public
    @Bean
    JestClient jestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setClientConfig(clientConfig());
        return factory.getObject();
    }
}
