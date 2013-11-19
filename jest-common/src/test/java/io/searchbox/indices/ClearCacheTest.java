package io.searchbox.indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class ClearCacheTest {

    @Test
    public void testBasicUrlGeneration() {
        ClearCache clearCache = new ClearCache.Builder().build();
        assertEquals("_all/_cache/clear", clearCache.getURI());
    }

    @Test
    public void testBasicUrlGenerationWithParameters() {
        ClearCache clearCache = new ClearCache.Builder().bloom(true).fieldData(false).build();
        assertEquals("_all/_cache/clear?bloom=true&field_data=false", clearCache.getURI());
    }

    @Test
    public void testMultiIndexUrlGenerationWithParameters() {
        ClearCache clearCache = new ClearCache.Builder().addIndex("tom").addIndex("jerry").bloom(true).build();
        assertEquals("jerry%2Ctom/_cache/clear?bloom=true", clearCache.getURI());
    }

}
