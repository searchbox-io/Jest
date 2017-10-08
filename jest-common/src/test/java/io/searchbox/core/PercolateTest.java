package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */


public class PercolateTest {

    @Test
    public void percolateDocument() {
        Percolate percolate = new Percolate.Builder("twitter", "percolateQuery", "{query}").build();
        assertEquals("POST", percolate.getRestMethodName());
        assertEquals("twitter/percolateQuery/_percolate", percolate.getURI());
    }

    @Test
    public void equals() {
        Percolate percolate1 = new Percolate.Builder("twitter", "percolateQuery", "{query}").build();
        Percolate percolate1Duplicate = new Percolate.Builder("twitter", "percolateQuery", "{query}").build();

        assertEquals(percolate1, percolate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Percolate percolate1 = new Percolate.Builder("twitter", "percolateQuery", "{query}").build();
        Percolate percolate2 = new Percolate.Builder("twitter", "percolateQuery", "{different_query}").build();

        assertNotEquals(percolate1, percolate2);
    }

}
