package io.searchbox.Indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class DeleteIndexTest {

    @Test
    public void deleteIndex() {
        DeleteIndex delete = new DeleteIndex("twitter");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter", delete.getURI());
    }

    @Test
    public void deleteType(){
        DeleteIndex delete = new DeleteIndex("twitter","tweet");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet", delete.getURI());

    }

}
