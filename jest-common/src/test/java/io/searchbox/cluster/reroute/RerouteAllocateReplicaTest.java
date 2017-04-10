package io.searchbox.cluster.reroute;

import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.*;

public class RerouteAllocateReplicaTest {

    @Test
    public void allowPrimaryTrue() throws JSONException {
        RerouteAllocateReplica allocateReplica = new RerouteAllocateReplica("index1", 1, "node1");

        assertEquals(allocateReplica.getType(), "allocate_replica");

        String actualJson = new Gson().toJson(allocateReplica.getData());
        String expectedJson = "{\"index\":\"index1\", \"shard\": 1, \"node\": \"node1\", \"allow_primary\": true}";
        JSONAssert.assertEquals(actualJson, expectedJson, false);
    }

    @Test
    public void allowPrimaryFalse() throws JSONException {
        RerouteAllocateReplica allocateReplica = new RerouteAllocateReplica("index1", 1, "node1");

        assertEquals(allocateReplica.getType(), "allocate_replica");

        String actualJson = new Gson().toJson(allocateReplica.getData());
        String expectedJson = "{\"index\":\"index1\", \"shard\": 1, \"node\": \"node1\", \"allow_primary\": false}";
        JSONAssert.assertEquals(actualJson, expectedJson, false);
    }

}