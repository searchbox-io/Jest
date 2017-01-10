package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CatNodesBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.NodesBuilder().build();
        assertEquals("application/json", cat.getHeader("accept"));
        assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.NodesBuilder().build();
        assertEquals("_cat/nodes", cat.getURI());
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.NodesBuilder().setParameter("v", "true").build();
        assertEquals("_cat/nodes?v=true", cat.getURI());
    }
}
