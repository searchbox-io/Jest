package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.core.settings.Settings;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class IndexTest {

    @Test
    public void indexDocumentWithVersion() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.VERSION.toString(), "2");
        Index index = new Index(document);
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1?version=2", index.getURI());
    }

    @Test
    public void indexDocumentWithRouting() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.ROUTING.toString(), "searchbox");
        Index index = new Index(document);
        assertEquals("POST", index.getRestMethodName());
        assertEquals("twitter/tweet/1?routing=searchbox", index.getURI());
    }

    @Test
    public void indexDocumentWithOp_type() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.OP_TYPE.toString(), "create");
        Index index = new Index(document);
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1?op_type=create", index.getURI());
    }

    @Test
    public void indexDocumentWithPercolate() {
        Document document = new Document("twitter", "tweet", "1");
        document.addSetting(Settings.PERCOLATE.toString(), "*");
        Index index = new Index(document);
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1?percolate=*", index.getURI());
    }

    @Test
    public void indexDocumentWithoutID() {
        Document document = new Document("twitter", "tweet");
        Index index = new Index(document);
        assertEquals("POST", index.getRestMethodName());
        assertEquals("twitter/tweet/", index.getURI());
    }

    @Test(expected = RuntimeException.class)
    public void indexDocumentWithUnValidDocument() {
        Document document = new Document(null, null);
        new Index(document);
    }


}
