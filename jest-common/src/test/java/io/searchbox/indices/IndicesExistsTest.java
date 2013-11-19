package io.searchbox.indices;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author ferhat sobay
 */
public class IndicesExistsTest {

    @Test
    public void indicesExists() {
        IndicesExists indicesExists = new IndicesExists.Builder("twitter").build();
        assertEquals("HEAD", indicesExists.getRestMethodName());
        assertEquals("twitter", indicesExists.getURI());
    }
}
