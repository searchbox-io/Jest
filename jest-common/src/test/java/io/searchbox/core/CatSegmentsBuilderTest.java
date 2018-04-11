package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Eran Shahar on 16/03/2017.
 */
public class CatSegmentsBuilderTest {

    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.ShardsBuilder().build();
        assertEquals("application/json", cat.getHeader("accept"));
        assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.SegmentsBuilder().build();
        assertEquals("_cat/segments", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenSingleIndexGiven() {
        Cat cat = new Cat.SegmentsBuilder().addIndex("testIndex").build();
        assertEquals("_cat/segments/testIndex", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndicesGiven() {
        Cat cat = new Cat.SegmentsBuilder().addIndex("testIndex1").addIndex("testIndex2").build();
        assertEquals("_cat/segments/testIndex1%2CtestIndex2", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.SegmentsBuilder().setParameter("v", "true").build();
        assertEquals("_cat/segments?v=true", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenHeadersParameterGiven() {
        Cat cat = new Cat.SegmentsBuilder().setParameter("h", "index,shard,prirep,segment,docs.count").build();
        assertEquals("_cat/segments?h=index%2Cshard%2Cprirep%2Csegment%2Cdocs.count", cat.getURI(ElasticsearchVersion.UNKNOWN));
    }

}
