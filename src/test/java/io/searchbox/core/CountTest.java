package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class CountTest {

    @Test
    public void getURIWithoutIndexAndType() {
        Count count = new Count.Builder(null).build();
        assertEquals("_all/_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Count count = new Count.Builder(null).addIndex("twitter").build();
        assertEquals("twitter/_count", count.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Count count = new Count.Builder(null).addIndex("twitter").addType("tweet").build();
        assertEquals("twitter/tweet/_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Count count = new Count.Builder(null).addIndex("twitter").addIndex("searchbox").build();
        assertEquals("twitter,searchbox/_count", count.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Count count = new Count.Builder(null)
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        assertEquals("twitter,searchbox/tweet,jest/_count", count.getURI());
    }

}
