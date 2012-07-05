package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class UpdateTest {

    @Test
    public void updateDocument(){
        Doc doc = new Doc("twitter", "tweet", "1");
        Update get = new Update(doc,new Object());
        assertEquals("POST", get.getRestMethodName());
        assertEquals("twitter/tweet/1/_update", get.getURI());
    }

    @Test
    public void updateDocumentWithoutDoc(){
        Update update = new Update("twitter", "tweet", "1",new Object());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("twitter/tweet/1/_update", update.getURI());
    }
}
