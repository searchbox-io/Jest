package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class GetTest {

    @Test
    public void getDocument() {
        Get get = new Get("twitter", "tweet", "1");
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1", get.getURI());
        assertFalse(get.isDefaultIndexEnabled());
        assertFalse(get.isDefaultTypeEnabled());
    }

    @Test
    public void getDoc() {
        Get get = new Get(new Doc("twitter", "tweet", "1"));
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1", get.getURI());
        assertFalse(get.isDefaultIndexEnabled());
        assertFalse(get.isDefaultTypeEnabled());
    }

    @Test
    public void getSingleDocWithField() {
        Doc doc = new Doc("twitter", "tweet", "1");
        doc.addField("field1");
        doc.addField("field2");
        Get get = new Get(doc);
        assertEquals("POST", get.getRestMethodName());
        assertEquals("_mget", get.getURI());
        String expectedData = "{\"docs\":[{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"fields\":[\"field1\",\"field2\"]}]}";
        String actualData = (String) get.getData();
        assertEquals(expectedData,actualData);
        assertFalse(get.isDefaultIndexEnabled());
        assertFalse(get.isDefaultTypeEnabled());
    }

    @Test
    public void getMultipleDocs() {
        Doc doc1 = new Doc("twitter", "tweet", "1");
        Doc doc2 = new Doc("twitter", "tweet", "2");
        Doc doc3 = new Doc("twitter", "tweet", "3");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        Get get = new Get(docs);
        assertEquals("POST", get.getRestMethodName());
        assertEquals("_mget", get.getURI());
        assertFalse(get.isDefaultIndexEnabled());
        assertFalse(get.isDefaultTypeEnabled());
    }

    @Test
    public void getDocumentWithDefaultIndex() {
        Get get = new Get("tweet", "1");
        assertEquals("GET", get.getRestMethodName());
        assertEquals("/tweet/1", get.getURI());
        assertTrue(get.isDefaultIndexEnabled());
        assertFalse(get.isDefaultTypeEnabled());
    }

    @Test
    public void getDocumentWithDefaultIndexWithMultipleIds() {
        Get get = new Get("tweet", new String[]{"1", "2", "3"});
        assertEquals("POST", get.getRestMethodName());
        assertEquals("/tweet/_mget", get.getURI());
        assertTrue(get.isDefaultIndexEnabled());
        assertFalse(get.isDefaultTypeEnabled());
    }

    @Test
    public void getDocumentWithMultipleIds() {
        Get get = new Get(new String[]{"1", "2", "3"});
        assertEquals("POST", get.getRestMethodName());
        assertEquals("/_mget", get.getURI());
        assertTrue(get.isDefaultIndexEnabled());
        assertTrue(get.isDefaultTypeEnabled());
    }



    @Test
    public void prepareMultiGet() {
        String expected = "{\"docs\":[{\"_id\":\"1\"},{\"_id\":\"2\"},{\"_id\":\"3\"}]}";
        String actual = (String) new Get().prepareMultiGet(new String[]{"1", "2", "3"});
        assertEquals(expected, actual);
    }

    @Test
    public void prepareMultiGetWithDocList() {
        List<Doc> docs = getTestDocList();
        String expected = "{\"docs\":[{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\"}" +
                ",{\"_index\":\"jest\",\"_type\":\"tweet\",\"_id\":\"2\"}" +
                ",{\"_index\":\"searchbox\",\"_type\":\"tweet\",\"_id\":\"3\"}]}";
        String actual = (String) new Get().prepareMultiGet(docs);
        assertEquals(expected, actual);
    }

    @Test
    public void prepareMultiGetWithDocListAndFields() {
        List<Doc> docs = getTestDocList();
        for(Doc doc:docs){
            doc.addField("field1");
            doc.addField("field2");
        }
        String expected = "{\"docs\":[{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"fields\":[\"field1\",\"field2\"]}" +
                ",{\"_index\":\"jest\",\"_type\":\"tweet\",\"_id\":\"2\",\"fields\":[\"field1\",\"field2\"]}" +
                ",{\"_index\":\"searchbox\",\"_type\":\"tweet\",\"_id\":\"3\",\"fields\":[\"field1\",\"field2\"]}]}";
        String actual = (String) new Get().prepareMultiGet(docs);
        assertEquals(expected, actual);
    }

    private   List<Doc> getTestDocList(){
        Doc doc1 = new Doc("twitter", "tweet", "1");
        Doc doc2 = new Doc("jest", "tweet", "2");
        Doc doc3 = new Doc("searchbox", "tweet", "3");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        return docs;
    }



}
