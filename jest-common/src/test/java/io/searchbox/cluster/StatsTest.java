package io.searchbox.cluster;

import io.searchbox.action.Action;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatsTest {
    @Test
    public void testUriGeneration() {
        Action action = new Stats.Builder().build();
        assertEquals("/_cluster/stats/nodes/_all", action.getURI());
    }

    @Test
    public void testUriGenerationWithSpecificNodes() {
        Action action = new Stats.Builder()
                .addNode("test1")
                .addNode("test2")
                .build();
        assertEquals("/_cluster/stats/nodes/test1,test2", action.getURI());
    }
}
