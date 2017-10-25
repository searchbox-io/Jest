package io.searchbox.cluster;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class NodesHotThreadsTest {

    @Test
    public void testUriGenerationWithAllNodes() {
        NodesHotThreads action = new NodesHotThreads.Builder().build();
        assertEquals("/_nodes/_all/hot_threads", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSingleNode() {
        NodesHotThreads action = new NodesHotThreads.Builder().addNode("Pony").build();
        assertEquals("/_nodes/Pony/hot_threads", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSingleNodeAndParameter() {
        NodesHotThreads action = new NodesHotThreads.Builder().addNode("Pony").interval("100ms").build();
        assertEquals("/_nodes/Pony/hot_threads?interval=100ms", action.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
