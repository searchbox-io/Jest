package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
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
        assertEquals("_cat/shards", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexGiven() {
        Cat cat = new Cat.ShardsBuilder().addIndex("testIndex").build();
        assertEquals("_cat/shards/testIndex", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.ShardsBuilder().setParameter("v", "true").build();
        assertEquals("_cat/shards?v=true", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
