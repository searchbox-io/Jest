package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Bartosz Polnik
 */
public class CatIndicesBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.IndicesBuilder().build();
        assertEquals("application/json", cat.getHeader("accept"));
        assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.IndicesBuilder().build();
        assertEquals("_cat/indices/_all", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexGiven() {
        Cat cat = new Cat.IndicesBuilder().addIndex("testIndex").build();
        assertEquals("_cat/indices/testIndex", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexAndTypeGiven() {
        Cat cat = new Cat.IndicesBuilder().addIndex("testIndex").addType("testType").build();
        assertEquals("_cat/indices/testIndex/testType", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.IndicesBuilder().setParameter("v", "true").build();
        assertEquals("_cat/indices/_all?v=true", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
