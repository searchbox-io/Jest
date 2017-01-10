package io.searchbox.cluster;

import com.google.gson.Gson;
import io.searchbox.cluster.reroute.RerouteMove;
import io.searchbox.cluster.reroute.RerouteCommand;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RerouteTest {

    @Test
    public void reroute() throws JSONException {
        List<RerouteCommand> moveCommands = new LinkedList<RerouteCommand>();
        moveCommands.add(new RerouteMove("index1", 1, "node1", "node2"));
        moveCommands.add(new RerouteMove("index2", 1, "node2", "node1"));

        Reroute reroute = new Reroute.Builder(moveCommands).build();
        assertEquals("/_cluster/reroute", reroute.getURI());
        assertEquals("POST", reroute.getRestMethodName());

        String expectedData = "{ \"commands\": [" +
                "{ \"move\": { \"index\": \"index1\", \"shard\": 1, \"from_node\": \"node1\", \"to_node\": \"node2\" } }, " +
                "{ \"move\": { \"index\": \"index2\", \"shard\": 1, \"from_node\": \"node2\", \"to_node\": \"node1\" } }" +
                "] }";
        JSONAssert.assertEquals(expectedData, reroute.getData(new Gson()), false);
    }

}