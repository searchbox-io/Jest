package io.searchbox.client.config;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class ClientConfig {

    private Set<String> serverList;
    private boolean isMultiThreaded;
    private boolean isDiscoveryEnabled;
    private long discoveryFrequency;
    private TimeUnit discoveryFrequencyTimeUnit;
    private Gson gson;

    private ClientConfig() {
    }

    public ClientConfig(AbstractBuilder builder) {
        this.serverList = builder.serverList;
        this.isMultiThreaded = builder.isMultiThreaded;
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

    public static class Builder extends AbstractBuilder<ClientConfig, Builder> {

        public Builder(ClientConfig clientConfig) {
            super(clientConfig);
        }

        public Builder(Collection<String> serverUris) {
            super(serverUris);
        }

        public Builder(String serverUri) {
            super(serverUri);
        }

        public ClientConfig build() {
            return new ClientConfig(this);
        }

    }

    protected static abstract class AbstractBuilder<T extends ClientConfig, K extends AbstractBuilder> {
        protected Set<String> serverList = new LinkedHashSet<String>();
        protected boolean isMultiThreaded;
        protected Integer maxTotalConnection;
        protected Integer defaultMaxTotalConnectionPerRoute;
        protected boolean isDiscoveryEnabled;
        protected long discoveryFrequency = 10L;
        protected TimeUnit discoveryFrequencyTimeUnit = TimeUnit.SECONDS;
        protected Gson gson;

        public AbstractBuilder(Collection<String> serverUris) {
            this.serverList.addAll(serverUris);
        }

        public AbstractBuilder(String serverUri) {
            this.serverList.add(serverUri);
        }

        public AbstractBuilder(ClientConfig clientConfig) {
            this.serverList = clientConfig.serverList;
            this.isMultiThreaded = clientConfig.isMultiThreaded;
            this.isDiscoveryEnabled = clientConfig.isDiscoveryEnabled;
            this.discoveryFrequency = clientConfig.discoveryFrequency;
            this.discoveryFrequencyTimeUnit = clientConfig.discoveryFrequencyTimeUnit;
            this.gson = clientConfig.gson;
        }

        public K addServer(String serverUri) {
            this.serverList.add(serverUri);
            return (K) this;
        }

        public K addServer(Collection<String> serverUris) {
            this.serverList.addAll(serverUris);
            return (K) this;
        }

        public K gson(Gson gson) {
            this.gson = gson;
            return (K) this;
        }

        public K discoveryFrequency(long discoveryFrequency, TimeUnit discoveryFrequencyTimeUnit) {
            this.discoveryFrequency = discoveryFrequency;
            this.discoveryFrequencyTimeUnit = discoveryFrequencyTimeUnit;
            return (K) this;
        }

        public K discoveryEnabled(boolean isDiscoveryEnabled) {
            this.isDiscoveryEnabled = isDiscoveryEnabled;
            return (K) this;
        }

        public K multiThreaded(boolean isMultiThreaded) {
            this.isMultiThreaded = isMultiThreaded;
            return (K) this;
        }

        public K maxTotalConnection(int maxTotalConnection) {
            this.maxTotalConnection = maxTotalConnection;
            return (K) this;
        }

        public K defaultMaxTotalConnectionPerRoute(int defaultMaxTotalConnectionPerRoute) {
            this.defaultMaxTotalConnectionPerRoute = defaultMaxTotalConnectionPerRoute;
            return (K) this;
        }

        abstract public T build();

    }

}
