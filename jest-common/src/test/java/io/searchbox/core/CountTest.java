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
        Count count = new Count.Builder().build();
        assertEquals("_all/_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Count count = new Count.Builder().addIndex("twitter").build();
        assertEquals("twitter/_count", count.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Count count = new Count.Builder().addIndex("twitter").addType("tweet").build();
        assertEquals("twitter/tweet/_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Count count = new Count.Builder().addIndex("twitter").addIndex("searchbox").build();
        assertEquals("twitter%2Csearchbox/_count", count.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Count count = new Count.Builder()
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        assertEquals("twitter%2Csearchbox/tweet%2Cjest/_count", count.getURI());
    }

}
