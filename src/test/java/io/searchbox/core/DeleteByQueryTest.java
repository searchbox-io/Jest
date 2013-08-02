package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */
public class DeleteByQueryTest {

    @Test
    public void getURIWithoutIndexAndType() {
        assertEquals("_all/_query", new DeleteByQuery.Builder(null).build().getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        assertEquals("twitter/_query", new DeleteByQuery.Builder(null).addIndex("twitter").build().getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        assertEquals("_all/tweet%2Cjest/_query", new DeleteByQuery.Builder(null).addType("tweet").addType("jest").build().getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        assertEquals("twitter/tweet/_query", new DeleteByQuery.Builder(null).addIndex("twitter").addType("tweet").build().getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        assertEquals("twitter%2Csearchbox/_query",
                new DeleteByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").build().getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        assertEquals("twitter%2Csearchbox/tweet%2Cjest/_query", new DeleteByQuery.Builder(null)
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build()
                .getURI());
    }

}
