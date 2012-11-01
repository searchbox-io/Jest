package io.searchbox.core;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.JestHttpClient;
import org.junit.Before;

import java.io.IOException;
import java.util.LinkedHashSet;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */

public class AbstractIntegrationTest {

	protected JestClientFactory factory;

	protected JestHttpClient client;

	@Before
	public void setUp() throws Exception {
		factory = new JestClientFactory();
		ClientConfig clientConfig = new ClientConfig();
		LinkedHashSet<String> servers = new LinkedHashSet<String>();
		servers.add("http://localhost:9200");
		clientConfig.getServerProperties().put(ClientConstants.SERVER_LIST, servers);
		clientConfig.getClientFeatures().put(ClientConstants.IS_MULTI_THREADED, true);

		factory.setClientConfig(clientConfig);

		client = (JestHttpClient) factory.getObject();
	}

}
