package io.searchbox.client.config.discovery;

import com.google.gson.Gson;
import io.searchbox.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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

    ClientConfig clientConfig;
    JestClient jestClient;

    @Before
    public void setUp() throws Exception {
        clientConfig = new ClientConfig.Builder("http://localhost:9200")
                .discoveryEnabled(true)
                .discoveryFrequency(1l, TimeUnit.SECONDS)
                .build();

        jestClient = mock(JestClient.class);

    }

    @Test
    public void testBasicFlow() throws Exception {

        NodeChecker nodeChecker = new NodeChecker(clientConfig, jestClient);


        JestResult result = new JestResult(new Gson());
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
