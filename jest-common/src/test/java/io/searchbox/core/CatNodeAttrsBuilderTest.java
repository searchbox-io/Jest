package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;

public class CatNodeAttrsBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.NodeAttrsBuilder().build();
        assertEquals("application/json", cat.getHeader("accept"));
        assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.NodeAttrsBuilder().build();
        assertEquals("_cat/nodeattrs", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.NodeAttrsBuilder().setParameter("h", "name,pid,attr,value").build();
        assertEquals("_cat/nodeattrs?h=name,pid,attr,value", URLDecoder.decode(cat.getURI(ElasticsearchVersion.UNKNOWN)));
    }
}