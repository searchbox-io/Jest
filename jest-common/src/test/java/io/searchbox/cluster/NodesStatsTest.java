package io.searchbox.cluster;

import io.searchbox.Action;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class NodesStatsTest {

    @Test
    public void testUriGeneration() throws Exception {
        Action action = new NodesStats.Builder()
                .build();
        assertEquals("/_nodes/_all/stats", action.getURI());
    }

    @Test
    public void testUriGenerationWithSingleNode() throws Exception {
        Action action = new NodesStats.Builder()
                .addNode("james")
                .clear(true)
                .build();
        assertEquals("/_nodes/james/stats/clear", action.getURI());
    }

}
