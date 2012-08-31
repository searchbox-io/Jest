package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Dogukan Sonmez
 */


public class DeleteTest {

    @Test
    public void deleteDocument() {
        Delete delete = new Delete.Builder("twitter", "tweet").id("1").build();
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

    @Test
    public void deleteType() {
        Delete delete = new Delete.Builder("twitter", "tweet").build();
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }
}
