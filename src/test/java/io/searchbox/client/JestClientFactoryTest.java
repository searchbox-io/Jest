package io.searchbox.client;

import io.searchbox.client.http.JestHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */


public class JestClientFactoryTest {

	private JestClientFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new JestClientFactory();
	}


	@Test
	public void clientCreation() {
		JestHttpClient jestClient = (JestHttpClient) factory.getObject();
		assertTrue(jestClient != null);
		assertTrue(jestClient.getHttpClient().getConnectionManager() instanceof PoolingClientConnectionManager);
		assertEquals(jestClient.getServers().size(), 1);
		assertTrue(jestClient.getServers().contains("http://localhost:9200"));
	}

	@Test
	public void clientCreationWithNullClientConfig() {
		JestHttpClient jestClient = (JestHttpClient) factory.getObject();
		assertTrue(jestClient != null);
		assertEquals(jestClient.getServers().size(), 1);
		assertTrue(jestClient.getServers().contains("http://localhost:9200"));
	}


}
