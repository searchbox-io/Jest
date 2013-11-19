package io.searchbox.core;

import io.searchbox.params.Parameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class DeleteTest {

    @Test
    public void deleteDocument() {
        Delete delete = new Delete.Builder("1")
                .index("twitter")
                .type("tweet")
                .build();
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1", delete.getURI());
    }

    @Test
    public void deleteDocumentWithVersion() {
        Delete delete = new Delete.Builder("1")
                .index("twitter")
                .type("tweet")
                .setParameter(Parameters.VERSION, 1)
                .build();
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1?version=1", delete.getURI());
    }
}
