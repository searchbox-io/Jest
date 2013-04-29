package io.searchbox.client.config;

/**
 * @author Dogukan Sonmez
 */


public class ClientConstants {

    private ClientConstants() {
    }

    @Deprecated
    public static final String SERVER_LIST = "serverList";

    @Deprecated
    public static final String IS_MULTI_THREADED = "isMultiThreaded";

    @Deprecated
    public static final String MAX_TOTAL_CONNECTION = "maxTotalConnection";

    @Deprecated
    public static final String DEFAULT_MAX_TOTAL_CONNECTION_PER_ROUTE = "defaultMaxTotalConnectionPerRoute";

    @Deprecated
    public static final String MAX_TOTAL_CONNECTION_PER_ROUTE = "maxTotalConnectionPerRoute";

    @Deprecated
    public static final String DISCOVERY_ENABLED = "isDiscoveryEnabled";    //boolean

    @Deprecated
    public static final String DISCOVERY_FREQUENCY = "discoveryFrequency";   //long

    @Deprecated
    public static final String DISCOVERY_FREQUENCY_TIMEUNIT = "discoveryFrequencyTimeUnit";  //TimeUnit
}
