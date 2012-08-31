package io.searchbox.Indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Dogukan Sonmez
 */


public class DeleteIndexTest {
    @Test
    public void deleteIndex() {
        DeleteIndex delete = new DeleteIndex("twitter");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

}
