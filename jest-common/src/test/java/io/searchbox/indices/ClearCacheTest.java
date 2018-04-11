package io.searchbox.indices;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author cihat keser
 */
public class ClearCacheTest {

    @Test
    public void testBasicUrlGeneration() {
        ClearCache clearCache = new ClearCache.Builder().build();
        assertEquals("_all/_cache/clear", clearCache.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testBasicUrlGenerationWithParameters() {
        ClearCache clearCache = new ClearCache.Builder().bloom(true).fieldData(false).build();
        assertEquals("_all/_cache/clear?bloom=true&field_data=false", clearCache.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testMultiIndexUrlGenerationWithParameters() {
        ClearCache clearCache = new ClearCache.Builder().addIndex("tom").addIndex("jerry").bloom(true).build();
        assertEquals("tom%2Cjerry/_cache/clear?bloom=true", clearCache.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equals(){
        ClearCache clearCache1 = new ClearCache.Builder().addIndex("twitter").bloom(true).fieldData(false).build();
        ClearCache clearCache1Duplicate = new ClearCache.Builder().addIndex("twitter").bloom(true).fieldData(false).build();

        assertEquals(clearCache1, clearCache1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentParameters(){
        ClearCache clearCache1 = new ClearCache.Builder().addIndex("twitter").bloom(true).fieldData(false).build();
        ClearCache clearCache2 = new ClearCache.Builder().addIndex("twitter").bloom(false).fieldData(true).build();

        assertNotEquals(clearCache1, clearCache2);
    }

}
