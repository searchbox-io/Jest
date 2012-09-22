package io.searchbox.client.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ClientConfig {

    private Map<String, Object> serverProperties = new HashMap<String, Object>();

    private Map<String, Object> clientFeatures = new HashMap<String, Object>();

    public Map<String, Object> getServerProperties() {
        return serverProperties;
    }

    public Map<String, Object> getClientFeatures() {
        return clientFeatures;
    }

    public Object getServerProperty(String propertyName) {
        return serverProperties.get(propertyName);
    }

    public Object getClientFeature(String featureName) {
        return clientFeatures.get(featureName);
    }

    public void setServerProperties(Map<String, Object> serverProperties) {
        this.serverProperties = serverProperties;
    }

    public void setClientFeatures(Map<String, Object> clientFeatures) {
        this.clientFeatures = clientFeatures;
    }
}
