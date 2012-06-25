package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.core.settings.Settings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class DeleteTest {

    @Test
    public void deleteDocumentWithVersioningOption() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.VERSION.toString(), "2");
        Delete delete = new Delete(document);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1?version=2", delete.getURI());
    }

    @Test
    public void deleteDocumentWithRoutingOption() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.ROUTING.toString(), "searchbox");
        Delete delete = new Delete(document);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1?routing=searchbox", delete.getURI());
    }

    @Test
    public void deleteDocumentWithParentOption() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.PARENT.toString(), "11111");
        Delete delete = new Delete(document);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1?parent=11111", delete.getURI());
    }

    @Test
    public void deleteDocumentWithMultipleOption() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.PARENT.toString(), "11111");
        document.addSetting(Settings.VERSION.toString(), "2");
        Delete delete = new Delete(document);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1?parent=11111&version=2", delete.getURI());
    }

    @Test
    public void deleteDocumentWithMultipleOptionDifferentThanSettings() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting("soft", "true");
        document.addSetting("count", "28");
        Delete delete = new Delete(document);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1?soft=true&count=28", delete.getURI());
    }

    @Test
    public void deleteDocumentWithoutId() {
        Document document = new Document("twitter", "tweet");
        Delete delete = new Delete(document);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/", delete.getURI());
    }

    @Test
    public void deleteIndexDocument() {
        Delete delete = new Delete("twitter");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter", delete.getURI());
    }

    @Test(expected = RuntimeException.class)
    public void deleteIndexWithInValidDocument() {
        new Delete(new Document(null,null));
    }



}
