package io.searchbox.cluster;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.JsonObject;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class StateIntegrationTest extends AbstractIntegrationTest {

    @Test
    @ElasticsearchIndex
    public void clusterState() throws IOException {
        JestResult result = client.execute(new State.Builder().build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull(resultJson);
        assertNotNull(resultJson.getAsJsonObject("nodes"));
        assertNotNull(resultJson.getAsJsonObject("routing_table"));
        assertNotNull(resultJson.getAsJsonObject("metadata"));
        assertNotNull(resultJson.getAsJsonObject("blocks"));
    }

    @Test
    @ElasticsearchIndex
    public void clusterStateWithFilterMetadata() throws IOException {
        JestResult result = client.execute(new State.Builder().filterMetadata(true).build());
        assertNotNull(result);
        assertTrue(result.isSucceeded());

        JsonObject resultJson = result.getJsonObject();
        assertNotNull(resultJson);
        assertNotNull(resultJson.getAsJsonObject("nodes"));
        assertNotNull(resultJson.getAsJsonObject("routing_table"));
        assertNull(resultJson.getAsJsonObject("metadata"));
        assertNotNull(resultJson.getAsJsonObject("blocks"));
    }

}
