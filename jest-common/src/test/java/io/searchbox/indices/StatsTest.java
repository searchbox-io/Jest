package io.searchbox.indices;

import org.junit.Test;

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

}