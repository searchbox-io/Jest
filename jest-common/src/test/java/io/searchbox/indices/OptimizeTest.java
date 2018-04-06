package io.searchbox.indices;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OptimizeTest {

    @Test
    public void testBasicUriGeneration() {
        Optimize optimize = new Optimize.Builder().addIndex("twitter").build();

        assertEquals("POST", optimize.getRestMethodName());
        assertEquals("twitter/_optimize", optimize.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        Optimize optimize1 = new Optimize.Builder().addIndex("twitter").build();
        Optimize optimize1Duplicate = new Optimize.Builder().addIndex("twitter").build();

        assertEquals(optimize1, optimize1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Optimize optimize1 = new Optimize.Builder().addIndex("twitter").build();
        Optimize optimize2 = new Optimize.Builder().addIndex("myspace").build();

        assertNotEquals(optimize1, optimize2);
    }

}