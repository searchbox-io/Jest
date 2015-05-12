package io.searchbox.client.config;

import com.google.gson.Gson;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 * @author Min Cha
 */
public class ClientConfig {
    private Set<String> serverList;
    private boolean isMultiThreaded;
    private boolean isDiscoveryEnabled;
    private boolean isRequestCompressionEnabled;
    private int connTimeout;
    private int readTimeout;
    private long discoveryFrequency;
    private long maxConnectionIdleTime;
    private TimeUnit discoveryFrequencyTimeUnit;
    private TimeUnit maxConnectionIdleTimeDurationTimeUnit;
    private Gson gson;

    private String defaultSchemeForDiscoveredNodes;

    private ClientConfig() {
    }

    public ClientConfig(AbstractBuilder builder) {
        this.serverList = builder.serverList;
        this.isMultiThreaded = builder.isMultiThreaded;
        this.isDiscoveryEnabled = builder.isDiscoveryEnabled;
        this.isRequestCompressionEnabled = builder.isRequestCompressionEnabled;
        this.discoveryFrequency = builder.discoveryFrequency;
        this.discoveryFrequencyTimeUnit = builder.discoveryFrequencyTimeUnit;
        this.connTimeout = builder.connTimeout;
        this.readTimeout = builder.readTimeout;
        this.maxConnectionIdleTime = builder.maxConnectionIdleTime;
        this.maxConnectionIdleTimeDurationTimeUnit = builder.maxConnectionIdleTimeDurationTimeUnit;
        this.gson = builder.gson;
        this.defaultSchemeForDiscoveredNodes = builder.defaultSchemeForDiscoveredNodes;
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

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnTimeout() {
        return connTimeout;
    }

    public long getMaxConnectionIdleTime() {
        return maxConnectionIdleTime;
    }

    public TimeUnit getMaxConnectionIdleTimeDurationTimeUnit() {
        return maxConnectionIdleTimeDurationTimeUnit;
    }

    public Gson getGson() {
        return gson;
    }

    public String getDefaultSchemeForDiscoveredNodes() {
        return defaultSchemeForDiscoveredNodes;
    }

    public boolean isRequestCompressionEnabled() {
        return isRequestCompressionEnabled;
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(serverList)
                .append(isMultiThreaded)
                .append(isDiscoveryEnabled)
                .append(isRequestCompressionEnabled)
                .append(discoveryFrequency)
                .append(connTimeout)
                .append(readTimeout)
                .append(discoveryFrequencyTimeUnit)
                .append(maxConnectionIdleTime)
                .append(maxConnectionIdleTimeDurationTimeUnit)
                .append(gson)
                .append(defaultSchemeForDiscoveredNodes)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        ClientConfig rhs = (ClientConfig) obj;
        return new EqualsBuilder()
                .append(serverList, rhs.serverList)
                .append(isMultiThreaded, rhs.isMultiThreaded)
                .append(isDiscoveryEnabled, rhs.isDiscoveryEnabled)
                .append(isRequestCompressionEnabled, rhs.isRequestCompressionEnabled)
                .append(discoveryFrequency, rhs.discoveryFrequency)
                .append(connTimeout, rhs.connTimeout)
                .append(readTimeout, rhs.readTimeout)
                .append(discoveryFrequencyTimeUnit, rhs.discoveryFrequencyTimeUnit)
                .append(maxConnectionIdleTime, rhs.maxConnectionIdleTime)
                .append(maxConnectionIdleTimeDurationTimeUnit, rhs.maxConnectionIdleTimeDurationTimeUnit)
                .append(gson, rhs.gson)
                .append(defaultSchemeForDiscoveredNodes, rhs.defaultSchemeForDiscoveredNodes)
                .isEquals();
    }

    protected static abstract class AbstractBuilder<T extends ClientConfig, K extends AbstractBuilder> {
        protected Set<String> serverList = new LinkedHashSet<String>();
        protected boolean isMultiThreaded;
        protected boolean isDiscoveryEnabled;
        protected boolean isRequestCompressionEnabled;
        protected long discoveryFrequency = 10L;
        protected long maxConnectionIdleTime = -1L;
        protected Integer maxTotalConnection;
        protected Integer defaultMaxTotalConnectionPerRoute;
        protected Integer connTimeout = 3000;
        protected Integer readTimeout = 3000;
        protected TimeUnit discoveryFrequencyTimeUnit = TimeUnit.SECONDS;
        protected TimeUnit maxConnectionIdleTimeDurationTimeUnit = TimeUnit.SECONDS;
        protected Gson gson;
        protected String defaultSchemeForDiscoveredNodes = "http://";

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
            this.connTimeout = clientConfig.connTimeout;
            this.readTimeout = clientConfig.readTimeout;
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

        /**
         * Whether to GZIP compress request bodies.
         * You also need to enable <code>http.compression</code> setting on your Elasticsearch nodes for this to work.
         */
        public K requestCompressionEnabled(boolean isRequestCompressionEnabled) {
            this.isRequestCompressionEnabled = isRequestCompressionEnabled;
            return (K) this;
        }

        public K connTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
            return (K) this;
        }
        
        public K readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
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

        public K maxConnectionIdleTime(long duration, TimeUnit maxConnectionIdleTimeDurationTimeUnit) {
            this.maxConnectionIdleTime = duration;
            this.maxConnectionIdleTimeDurationTimeUnit = maxConnectionIdleTimeDurationTimeUnit;
            return (K) this;
        }

        /**
         * The default URI scheme to use for discovered nodes.
         * @param defaultSchemeForDiscoveredNodes a valid URI scheme like <code>http</code> or <code>https</code>
         */
        public K defaultSchemeForDiscoveredNodes(String defaultSchemeForDiscoveredNodes) {
            this.defaultSchemeForDiscoveredNodes = defaultSchemeForDiscoveredNodes + "://";
            return (K) this;
        }

        abstract public T build();

    }
}
