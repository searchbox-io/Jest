package io.searchbox.client.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ClientConfig {

    private final Map<String, Object> serverProperties = new HashMap<String, Object>();

    private final Map<String,Object> clientFeatures = new HashMap<String, Object>();

    public Map<String, Object> getServerProperties() {
        return serverProperties;
    }

    public Map<String, Object> getClientFeatures() {
        return clientFeatures;
    }

    public Object getServerProperty(String propertyName){
        return serverProperties.get(propertyName);
    }

    public Object getClientFuture(String futureName){
        return clientFeatures.get(futureName);
    }
}
