package io.searchbox.client.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ClientConfig {

    private Map<String, Object> properties = new HashMap<String, Object>();

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
