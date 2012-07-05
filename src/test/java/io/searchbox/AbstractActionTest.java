package io.searchbox;

import io.searchbox.core.*;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class AbstractActionTest {

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        String actual = new Delete("test").buildURI(new Doc("twitter", "tweet", "1"));
        assertEquals(expected, actual);
    }

    @Test
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get("twitter", "tweet", "1");
        assertEquals("GET", get.getRestMethodName());

        Delete del = new Delete("Silvester", "Stallone", "2");
        assertEquals("DELETE", del.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());

        Percolate percolate = new Percolate("Celtic", "Boston", "{\"Really good query\"}");
        assertEquals("PUT", percolate.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());

    }

    @Test
    public void requestDataMultipleClientRequest() {
        Doc doc = new Doc("indexName", "indexType", "1");
        Index indexDocument = new Index("indexName", "indexType", "id","\"indexDocumentData\"");
        Update update = new Update(doc,"\"updateData\"");

        assertEquals("\"updateData\"", update.getData().toString());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("update/index/1/_update", update.getURI());

        assertEquals("\"indexDocumentData\"", indexDocument.getData().toString());
        assertEquals("PUT", indexDocument.getRestMethodName());
        assertEquals("indexName/indexType/id", indexDocument.getURI());
    }

    @Test
    public void buildQueryStringWithEmptySettings() {
        assertEquals("", new Index(new Object()).buildQueryString(new HashMap<String, Object>()));
    }

    @Test
    public void buildQueryStringWithSettings() {
        HashMap<String,Object> map = new HashMap<String,Object>();
        assertEquals("?version=2", new Index(new Object()).buildQueryString(map));
    }

}