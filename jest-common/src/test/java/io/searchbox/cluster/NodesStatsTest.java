package io.searchbox.cluster;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class NodesStatsTest {

    @Test
    public void testUriGeneration() throws Exception {
        NodesStats action = new NodesStats.Builder()
                .build();
        assertEquals("/_nodes/_all/stats", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testUriGenerationWithSingleNode() throws Exception {
        NodesStats action = new NodesStats.Builder()
                .addNode("james")
                .withOs()
                .withJvm()
                .build();
        assertEquals("/_nodes/james/stats/os,jvm", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

}
