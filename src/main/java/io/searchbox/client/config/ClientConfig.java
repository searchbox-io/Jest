package io.searchbox.client.config;

import com.google.gson.Gson;
import org.apache.http.conn.routing.HttpRoute;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class ClientConfig {

    private Set<String> serverList;
    private boolean isMultiThreaded;
    private Integer maxTotalConnection;
    private Integer defaultMaxTotalConnectionPerRoute;
    private Map<HttpRoute, Integer> maxTotalConnectionPerRoute;
    private boolean isDiscoveryEnabled;
    private long discoveryFrequency;
    private TimeUnit discoveryFrequencyTimeUnit;
    private Gson gson;

    private ClientConfig() {
    }

    public ClientConfig(Builder builder) {
        this.serverList = builder.serverList;
        this.isMultiThreaded = builder.isMultiThreaded;
        this.maxTotalConnection = builder.maxTotalConnection;
        this.defaultMaxTotalConnectionPerRoute = builder.defaultMaxTotalConnectionPerRoute;
        this.maxTotalConnectionPerRoute = builder.maxTotalConnectionPerRoute;
        this.isDiscoveryEnabled = builder.isDiscoveryEnabled;
        this.discoveryFrequency = builder.discoveryFrequency;
        this.discoveryFrequencyTimeUnit = builder.discoveryFrequencyTimeUnit;
        this.gson = builder.gson;
    }

    public Set<String> getServerList() {
        return serverList;
    }

    public boolean isMultiThreaded() {
        return isMultiThreaded;
    }

    public Integer getMaxTotalConnection() {
        return maxTotalConnection;
    }

    public Integer getDefaultMaxTotalConnectionPerRoute() {
        return defaultMaxTotalConnectionPerRoute;
    }

    public Map<HttpRoute, Integer> getMaxTotalConnectionPerRoute() {
        return maxTotalConnectionPerRoute;
    }

    public boolean isDiscoveryEnabled() {
        return isDiscoveryEnabled;
    }

    public Long getDiscoveryFrequency() {
        return discoveryFrequency;
    }

    public TimeUnit getDiscoveryFrequencyTimeUnit() {
        return discoveryFrequencyTimeUnit;
    }

    public Gson getGson() {
        return gson;
    }

    public static class Builder {
        private Set<String> serverList = new LinkedHashSet<String>();
        private boolean isMultiThreaded;
        private Integer maxTotalConnection;
        private Integer defaultMaxTotalConnectionPerRoute;
        private Map<HttpRoute, Integer> maxTotalConnectionPerRoute = new HashMap<HttpRoute, Integer>();
        private boolean isDiscoveryEnabled;
        private long discoveryFrequency;
        private TimeUnit discoveryFrequencyTimeUnit;
        private Gson gson;

        public Builder(ClientConfig clientConfig) {
            this.serverList = clientConfig.serverList;
            this.isMultiThreaded = clientConfig.isMultiThreaded;
            this.maxTotalConnection = clientConfig.maxTotalConnection;
            this.defaultMaxTotalConnectionPerRoute = clientConfig.defaultMaxTotalConnectionPerRoute;
            this.maxTotalConnectionPerRoute = clientConfig.maxTotalConnectionPerRoute;
            this.isDiscoveryEnabled = clientConfig.isDiscoveryEnabled;
            this.discoveryFrequency = clientConfig.discoveryFrequency;
            this.discoveryFrequencyTimeUnit = clientConfig.discoveryFrequencyTimeUnit;
            this.gson = clientConfig.gson;
        }

        public Builder(Collection<String> serverUris) {
            this.serverList.addAll(serverUris);
        }

        public Builder(String serverUri) {
            this.serverList.add(serverUri);
        }

        public Builder addServer(String serverUri) {
            this.serverList.add(serverUri);
            return this;
        }

        public Builder addServer(Collection<String> serverUris) {
            this.serverList.addAll(serverUris);
            return this;
        }

        public Builder gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public Builder discoveryFrequency(long discoveryFrequency, TimeUnit discoveryFrequencyTimeUnit) {
            this.discoveryFrequency = discoveryFrequency;
            this.discoveryFrequencyTimeUnit = discoveryFrequencyTimeUnit;
            return this;
        }

        public Builder discoveryEnabled(boolean isDiscoveryEnabled) {
            this.isDiscoveryEnabled = isDiscoveryEnabled;
            return this;
        }

        public Builder multiThreaded(boolean isMultiThreaded) {
            this.isMultiThreaded = isMultiThreaded;
            return this;
        }

        public Builder maxTotalConnection(int maxTotalConnection) {
            this.maxTotalConnection = maxTotalConnection;
            return this;
        }

        public Builder defaultMaxTotalConnectionPerRoute(int defaultMaxTotalConnectionPerRoute) {
            this.defaultMaxTotalConnectionPerRoute = defaultMaxTotalConnectionPerRoute;
            return this;
        }

        public Builder maxTotalConnectionPerRoute(Map<HttpRoute, Integer> maxTotalConnectionPerRoute) {
            this.maxTotalConnectionPerRoute.putAll(maxTotalConnectionPerRoute);
            return this;
        }

        public Builder maxTotalConnectionPerRoute(HttpRoute httpRoute, int maxTotalConnection) {
            this.maxTotalConnectionPerRoute.put(httpRoute, maxTotalConnection);
            return this;
        }

        public ClientConfig build() {
            return new ClientConfig(this);
        }
    }

}
