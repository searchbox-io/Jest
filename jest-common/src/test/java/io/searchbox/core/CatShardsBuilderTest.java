package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CatShardsBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.ShardsBuilder().build();
        assertEquals("application/json", cat.getHeader("accept"));
        assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.ShardsBuilder().build();
        assertEquals("_cat/shards", cat.getURI());
    }

    @Test
    public void shouldGenerateValidUriWhenSingleIndexGiven() {
        Cat cat = new Cat.ShardsBuilder().addIndex("testIndex").build();
        assertEquals("_cat/shards/testIndex", cat.getURI());
    }

    @Test
    public void shouldGenerateValidUriWhenIndicesGiven() {
        Cat cat = new Cat.ShardsBuilder().addIndex("testIndex1").addIndex("testIndex2").build();
        assertEquals("_cat/shards/testIndex1%2CtestIndex2", cat.getURI());
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.ShardsBuilder().setParameter("v", "true").build();
        assertEquals("_cat/shards?v=true", cat.getURI());
    }
}
