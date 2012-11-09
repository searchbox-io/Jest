package io.searchbox.indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author ferhat sobay
 */
public class IndicesExistsTest {

    @Test
    public void indicesExists() {
        IndicesExists indicesExists = new IndicesExists("twitter");
        assertEquals("HEAD", indicesExists.getRestMethodName());
        assertEquals("twitter", indicesExists.getURI());
    }
}
