package io.searchbox.indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FlushTest {

    @Test
    public void testBasicUriGeneration() {
        Flush flush = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();

        assertEquals("POST", flush.getRestMethodName());
        assertEquals("twitter%2Cmyspace/_flush", flush.getURI());
    }

    @Test
    public void equalsReturnsTrueForSameIndices() {
        Flush flush1 = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();
        Flush flush1Duplicate = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();

        assertEquals(flush1, flush1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndices() {
        Flush flush1 = new Flush.Builder().addIndex("twitter").addIndex("myspace").build();
        Flush flush2 = new Flush.Builder().addIndex("myspace").build();

        assertNotEquals(flush1, flush2);
    }

}