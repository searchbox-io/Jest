package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.Source;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class AbstractActionTest {

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        String actual = new Delete("test").buildURI(new Document("twitter", "tweet", "1"));
        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithUValidParameters() {
        new Delete("test").buildURI(new Document("", "^^^^^^^", ""));
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithNullParameters() {
        new Delete("test").buildURI(null);
    }

    @Test
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get(new Document("twitter", "tweet", "1"));
        assertEquals("GET", get.getRestMethodName());

        Delete del = new Delete(new Document("Silvester", "Stallone", "2"));
        assertEquals("DELETE", del.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());

        Percolate percolate = new Percolate("Celtic", "Boston", "{\"Really good query\"}");
        assertEquals("PUT", percolate.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());

    }

    @Test
    public void requestDataMultipleClientRequest() {
        Document document = new Document("indexName", "indexType", "id");
        document.setSource(new Source("indexDocumentData"));
        Index indexDocument = new Index(document);

        Document document1 = new Document("update", "index", "1");
        document1.setSource(new Source("updateData"));
        Update update = new Update(document1);

        assertEquals("updateData", update.getData());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("update/index/1/_update", update.getURI());

        assertEquals("indexDocumentData", indexDocument.getData());
        assertEquals("PUT", indexDocument.getRestMethodName());
        assertEquals("indexName/indexType/id", indexDocument.getURI());

    }


}