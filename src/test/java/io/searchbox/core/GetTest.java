package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.core.settings.Settings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class GetTest {

    @Test
    public void getDocument(){
        Document document = new Document("twitter", "tweet", "1");
        Get get = new Get(document);
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1", get.getURI());
    }

    @Test
    public void getDocumentWithFields(){
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.FIELDS.toString(),"title,content");
        Get get = new Get(document);
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1?fields=title,content", get.getURI());
    }

    @Test
    public void getDocumentWithRouting(){
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.ROUTING.toString(),"kimcy");
        Get get = new Get(document);
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1?routing=kimcy", get.getURI());
    }
}
