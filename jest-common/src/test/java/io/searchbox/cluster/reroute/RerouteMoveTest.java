package io.searchbox.cluster.reroute;

import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.*;

public class RerouteMoveTest {

    @Test
    public void move() throws JSONException {
        RerouteMove rerouteMove = new RerouteMove("index1", 1, "node1", "node2");

        assertEquals(rerouteMove.getType(), "move");

        String actualJson = new Gson().toJson(rerouteMove.getData());
        String expectedJson = "{\"index\":\"index1\", \"shard\": 1, \"from_node\": \"node1\", \"to_node\": \"node2\"}";
        JSONAssert.assertEquals(actualJson, expectedJson, false);
    }

}