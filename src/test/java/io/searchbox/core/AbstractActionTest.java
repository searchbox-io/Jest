package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.Source;
import io.searchbox.core.settings.Settings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Document document = new Document("indexName", "indexType", "id");
        document.setSource(new Source("indexDocumentData"));
        Index indexDocument = new Index(document);

        Document document1 = new Document("update", "index", "1");
        document1.setSource(new Source("updateData"));
        Update update = new Update(document1);

        assertEquals("\"updateData\"", update.getData().toString());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("update/index/1/_update", update.getURI());

        assertEquals("\"indexDocumentData\"", indexDocument.getData().toString());
        assertEquals("PUT", indexDocument.getRestMethodName());
        assertEquals("indexName/indexType/id", indexDocument.getURI());
    }

    @Test
    public void buildQueryStringWithEmptySettings() {
        assertEquals("", new Index(new Document("twitter", "tweet", "1")).buildQueryString(new HashMap<String, Object>()));
    }

    @Test
    public void buildQueryStringWithSettings() {
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put(Settings.VERSION.toString(),"2");
        assertEquals("?version=2", new Index(new Document("twitter", "tweet", "1")).buildQueryString(map));
    }

    @Test
    public void isValid(){
        Doc doc = new Doc("twitter","tweet","1");
        assertTrue(new Index().isValid(doc));
    }


    @Test
    public void isValidWithoutType(){
        Doc doc = new Doc("twitter",null,"1");
        assertFalse(new Index().isValid(doc));
    }

    @Test
    public void isValidWithoutIndex(){
        Doc doc = new Doc(null,"type","1");
        assertFalse(new Index().isValid(doc));
    }

    @Test
    public void isValidWitInValidDoc(){
        Doc doc = new Doc(null,null,null);
        assertFalse(new Index().isValid(doc));
    }

    @Test
    public void isValidWitEmptyDoc(){
        Doc doc = new Doc("","","");
        assertFalse(new Index().isValid(doc));
    }


}