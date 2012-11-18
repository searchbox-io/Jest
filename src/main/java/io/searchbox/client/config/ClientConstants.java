package io.searchbox.client.config;

/**
 * @author Dogukan Sonmez
 */


public class ClientConstants {

	private ClientConstants() {
	}

	public static final String SERVER_LIST = "serverList";

	public static final String IS_MULTI_THREADED = "isMultiThreaded";

	public static final String DISCOVERY_ENABLED = "isDiscoveryEnabled";    //boolean

	public static final String DISCOVERY_FREQUENCY = "discoveryFrequency";   //long

	public static final String DISCOVERY_FREQUENCY_TIMEUNIT = "discoveryFrequencyTimeUnit";  //TimeUnit
}
