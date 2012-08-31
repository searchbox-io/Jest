package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class UpdateTest {

    @Test
    public void updateDocumentWithoutDoc(){
        Update update = new Update.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        assertEquals("POST", update.getRestMethodName());
        assertEquals("twitter/tweet/1/_update", update.getURI());
    }
}
