package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CatAllocationBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.AllocationBuilder().build();
        assertEquals("application/json", cat.getHeader("accept"));
        assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.AllocationBuilder().build();
        assertEquals("_cat/allocation", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenSingleNodeGiven() {
        Cat cat = new Cat.AllocationBuilder().addNode("testNode").build();
        assertEquals("_cat/allocation/testNode", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenNodesGiven() {
        Cat cat = new Cat.AllocationBuilder().addNode("testNode1").addNode("testNode2").build();
        assertEquals("_cat/allocation/testNode1%2CtestNode2", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.AllocationBuilder().setParameter("v", "true").build();
        assertEquals("_cat/allocation?v=true", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenHeadersParameterGiven() {
        Cat cat = new Cat.AllocationBuilder().setParameter("h", "shards,disk.indices,disk.used").build();
        assertEquals("_cat/allocation?h=shards%2Cdisk.indices%2Cdisk.used", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
