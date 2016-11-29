package io.searchbox.indices;

import io.searchbox.action.Action;
import org.junit.Test;

import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StatsTest {

    @Test
    public void testBasicUriGeneration() {
        Stats stats = new Stats.Builder().addIndex("twitter").build();

        assertEquals("GET", stats.getRestMethodName());
        assertEquals("twitter/_stats", stats.getURI());
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        Stats stats1 = new Stats.Builder().addIndex("twitter").build();
        Stats stats1Duplicate = new Stats.Builder().addIndex("twitter").build();

        assertEquals(stats1, stats1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Stats stats1 = new Stats.Builder().addIndex("twitter").build();
        Stats stats2 = new Stats.Builder().addIndex("myspace").build();

        assertNotEquals(stats1, stats2);
    }

    @Test
    public void testUriGenerationWithStatsFields() throws Exception {
        Action action = new Stats.Builder()
                .flush(true)
                .indexing(true)
                .search(true, "group1", "group2")
                .build();
        assertEquals("_all/_stats/flush,indexing,search?groups=group1,group2", URLDecoder.decode(action.getURI()));
    }

    @Test
    public void testUriGenerationWhenRemovingStatsFields() throws Exception {
        Action action = new Stats.Builder()
                .flush(true)
                .indexing(true)
                .indexing(false)
                .build();
        assertEquals("_all/_stats/flush", URLDecoder.decode(action.getURI()));
    }

}