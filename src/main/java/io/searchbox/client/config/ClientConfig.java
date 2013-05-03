package io.searchbox.client.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.conn.routing.HttpRoute;

/**
 * @author Dogukan Sonmez
 */


public class ClientConfig {

    private Map<String, Object> properties = new HashMap<String, Object>(){
      private static final long serialVersionUID = 1L;

      @SuppressWarnings({ "unchecked", "deprecation" })
      public Object put(String key, Object value) {
        if(ClientConstants.SERVER_LIST.equals(key)) {
          setServerList((LinkedHashSet<String>) value);
        }
        if(ClientConstants.DEFAULT_MAX_TOTAL_CONNECTION_PER_ROUTE.equals(key)) {
          setDefaultMaxTotalConnectionPerRoute((Integer) value);
        }
        if(ClientConstants.DISCOVERY_ENABLED.equals(key)) {
          setDiscoveryEnabled((Boolean) value);
        }
        if(ClientConstants.DISCOVERY_FREQUENCY.equals(key)) {
          setDiscoveryFrequency((Long) value);
        }
        if(ClientConstants.DISCOVERY_FREQUENCY_TIMEUNIT.equals(key)) {
          setDiscoveryFrequencyTimeUnit((TimeUnit) value);
        }
        if(ClientConstants.IS_MULTI_THREADED.equals(key)) {
          setMultiThreaded((Boolean) value);
        }
        if(ClientConstants.MAX_TOTAL_CONNECTION.equals(key)) {
          setMaxTotalConnection((Integer) value);
        }
        if(ClientConstants.MAX_TOTAL_CONNECTION_PER_ROUTE.equals(key)) {
          setMaxTotalConnectionPerRoute((Map<HttpRoute, Integer>) value);
        }
        return super.put(key, value);
      };
    };

    private LinkedHashSet<String> serverList;


    private Boolean isMultiThreaded;


    private Integer maxTotalConnection;


    private Integer defaultMaxTotalConnectionPerRoute;


    private Map<HttpRoute, Integer>  maxTotalConnectionPerRoute;


    private Boolean isDiscoveryEnabled;    //boolean


    private Long discoveryFrequency;   //long


    private TimeUnit discoveryFrequencyTimeUnit;  //TimeUnit     A             A


    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
    }


    public LinkedHashSet<String> getServerList() {
      return serverList;
    }

    public void setServerList(LinkedHashSet<String> serverList) {
      this.serverList = serverList;
    }


    public Boolean isMultiThreaded() {
      return isMultiThreaded;
    }

    public void setMultiThreaded(Boolean isMultiThreaded) {
      this.isMultiThreaded = isMultiThreaded;
    }


    public Integer getMaxTotalConnection() {
      return maxTotalConnection;
    }

    public void setMaxTotalConnection(Integer maxTotalConnection) {
      this.maxTotalConnection = maxTotalConnection;
    }


    public Integer getDefaultMaxTotalConnectionPerRoute() {
      return defaultMaxTotalConnectionPerRoute;
    }

    public void setDefaultMaxTotalConnectionPerRoute(Integer defaultMaxTotalConnectionPerRoute) {
      this.defaultMaxTotalConnectionPerRoute = defaultMaxTotalConnectionPerRoute;
    }


    public Map<HttpRoute, Integer> getMaxTotalConnectionPerRoute() {
      return maxTotalConnectionPerRoute;
    }

    public void setMaxTotalConnectionPerRoute(Map<HttpRoute, Integer> maxTotalConnectionPerRoute) {
      this.maxTotalConnectionPerRoute = maxTotalConnectionPerRoute;
    }


    public Boolean isDiscoveryEnabled() {
      return isDiscoveryEnabled;
    }

    public void setDiscoveryEnabled(Boolean isDiscoveryEnabled) {
      this.isDiscoveryEnabled = isDiscoveryEnabled;
    }


    public Long getDiscoveryFrequency() {
      return discoveryFrequency;
    }

    public void setDiscoveryFrequency(Long discoveryFrequency) {
      this.discoveryFrequency = discoveryFrequency;
    }


    public TimeUnit getDiscoveryFrequencyTimeUnit() {
      return discoveryFrequencyTimeUnit;
    }

    public void setDiscoveryFrequencyTimeUnit(TimeUnit discoveryFrequencyTimeUnit) {
      this.discoveryFrequencyTimeUnit = discoveryFrequencyTimeUnit;
    }



}
