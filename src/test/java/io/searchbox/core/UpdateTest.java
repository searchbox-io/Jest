package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.core.settings.Settings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class UpdateTest {

    @Test
    public void updateDocument(){
        Document document = new Document("twitter", "tweet", "1");
        Update get = new Update(document);
        assertEquals("POST", get.getRestMethodName());
        assertEquals("twitter/tweet/1/_update", get.getURI());
    }

    @Test
    public void updateDocumentWithSettings(){
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.ROUTING.toString(),"kimcy");
        Update update = new Update(document);
        assertEquals("POST", update.getRestMethodName());
        assertEquals("twitter/tweet/1/_update?routing=kimcy", update.getURI());
    }
}
