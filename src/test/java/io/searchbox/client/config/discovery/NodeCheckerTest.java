package io.searchbox.client.config.discovery;

import io.searchbox.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;
import io.searchbox.client.http.JestHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 */
public class NodeCheckerTest {

    final static Logger logger = LoggerFactory.getLogger(JestHttpClient.class);

    ClientConfig clientConfig;
    JestClient jestClient;

    @Before
    public void setUp() throws Exception {
        clientConfig = new ClientConfig();
        LinkedHashSet<String> servers = new LinkedHashSet<String>();
        servers.add("http://localhost:9200");
        clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);

        //enable host discovery
        clientConfig.getProperties().put(ClientConstants.DISCOVERY_ENABLED, true);
        clientConfig.getProperties().put(ClientConstants.DISCOVERY_FREQUENCY, 1l);
        clientConfig.getProperties().put(ClientConstants.DISCOVERY_FREQUENCY_TIMEUNIT, TimeUnit.SECONDS);

        jestClient = mock(JestClient.class);

    }

    @Test
    public void testBasicFlow() throws Exception {

        NodeChecker nodeChecker = new NodeChecker(clientConfig, jestClient);


        JestResult result = new JestResult();
        result.setJsonMap(getResultMap());
        when(jestClient.execute(isA(Action.class))).thenReturn(result);


        nodeChecker.runOneIteration();

        verify(jestClient).execute(isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        verify(jestClient).setServers(argument.capture());
        verifyNoMoreInteractions(jestClient);


        assertTrue(argument.getValue().contains("http://192.168.2.7:9200"));


    }

    private Map<String, Object> getResultMap() {

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> nodes = new HashMap<String, Object>();
        Map<String, Object> node = new HashMap<String, Object>();

        node.put("name", "Morbius");
        node.put("transport_address", "inet[/192.168.2.7:9300]");
        node.put("hostname", "asd.local");
        node.put("http_address", "inet[/192.168.2.7:9200]");

        nodes.put("node_name", node);
        result.put("nodes", nodes);
        result.put("ok", "true");

        return result;
    }
}
