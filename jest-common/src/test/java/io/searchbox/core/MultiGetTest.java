package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class MultiGetTest {

    @Test
    public void getMultipleDocs() {
        Doc doc1 = new Doc("twitter", "tweet", "1");
        Doc doc2 = new Doc("twitter", "tweet", "2");
        Doc doc3 = new Doc("twitter", "tweet", "3");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        MultiGet get = new MultiGet.Builder.ByDoc(docs).build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("/_mget", get.getURI());

    }

    @Test
    public void getDocumentWithMultipleIds() {
        MultiGet get = new MultiGet.Builder.ById("twitter", "tweet").addId(Arrays.asList("1", "2", "3")).build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/_mget", get.getURI());
    }

    @Test
    public void prepareMultiGet() {
        String expected = "{\"docs\":[{\"_id\":\"1\"},{\"_id\":\"2\"},{\"_id\":\"3\"}]}";
        String actual = (String) MultiGet.prepareMultiGet(new String[]{"1", "2", "3"});
        assertEquals(expected, actual);
    }

    @Test
    public void prepareMultiGetWithDocList() {
        List<Doc> docs = getTestDocList();
        String expected = "{\"docs\":[{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\"}" +
                ",{\"_index\":\"jest\",\"_type\":\"tweet\",\"_id\":\"2\"}" +
                ",{\"_index\":\"searchbox\",\"_type\":\"tweet\",\"_id\":\"3\"}]}";
        String actual = (String) MultiGet.prepareMultiGet(docs);
        assertEquals(expected, actual);
    }

    @Test
    public void prepareMultiGetWithDocListAndFields() {
        List<Doc> docs = getTestDocList();
        for (Doc doc : docs) {
            doc.addField("field1");
            doc.addField("field2");
        }
        String expected = "{\"docs\":[{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\",\"fields\":[\"field1\",\"field2\"]}" +
                ",{\"_index\":\"jest\",\"_type\":\"tweet\",\"_id\":\"2\",\"fields\":[\"field1\",\"field2\"]}" +
                ",{\"_index\":\"searchbox\",\"_type\":\"tweet\",\"_id\":\"3\",\"fields\":[\"field1\",\"field2\"]}]}";
        String actual = (String) MultiGet.prepareMultiGet(docs);
        assertEquals(expected, actual);
    }

    private List<Doc> getTestDocList() {
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
