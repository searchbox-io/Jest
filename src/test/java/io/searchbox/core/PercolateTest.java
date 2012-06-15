package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class PercolateTest {

    @Test
    public void percolateDocument(){
        Percolate percolate = new Percolate("twitter","percolateQuery","{query}");
        assertEquals("PUT",percolate.getRestMethodName());
        assertEquals("_percolator/twitter/percolateQuery",percolate.getURI());
    }
}
