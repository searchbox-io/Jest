package io.searchbox.indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StatusTest {

    @Test
    public void testBasicUriGeneration() {
        Status status = new Status.Builder().addIndex("twitter").build();

        assertEquals("GET", status.getRestMethodName());
        assertEquals("twitter/_status", status.getURI());
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        Status status1 = new Status.Builder().addIndex("twitter").build();
        Status status1Duplicate = new Status.Builder().addIndex("twitter").build();

        assertEquals(status1, status1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        Status status1 = new Status.Builder().addIndex("twitter").build();
        Status status2 = new Status.Builder().addIndex("myspace").build();

        assertNotEquals(status1, status2);
    }

}