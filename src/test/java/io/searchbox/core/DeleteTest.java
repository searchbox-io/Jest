package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */


public class DeleteTest {

    @Test
    public void deleteDocument() {
        Delete delete = new Delete("twitter", "tweet", "1");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

    @Test
    public void deleteType() {
        Delete delete = new Delete("twitter", "tweet");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

    @Test
    public void deleteIndex() {
        Delete delete = new Delete("twitter");
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

    @Test
    public void deleteDoc() {
        Doc doc = new Doc("twitter", "tweet", "1");
        Delete delete = new Delete(doc);
        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet/1", delete.getURI());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

    @Test
    public void deleteDocs() {
        Doc doc1 = new Doc("twitter", "tweet", "1");
        Doc doc2 = new Doc("twitter", "tweet", "2");
        Doc doc3 = new Doc("twitter", "tweet", "3");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        Delete delete = new Delete(docs);
        assertEquals("POST", delete.getRestMethodName());
        assertEquals("_bulk", delete.getURI());
        String data = "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\" } }\n"+
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"2\" } }\n"+
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"3\" } }\n";
        assertEquals(data,delete.getData());
        assertFalse(delete.isDefaultIndexEnabled());
        assertFalse(delete.isDefaultTypeEnabled());
    }

    @Test
    public void deleteDocsWithIds() {
        Delete delete = new Delete(new String[]{"1","2","3"});
        assertEquals("POST", delete.getRestMethodName());
        assertEquals("_bulk", delete.getURI());
        String data = "{ \"delete\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"<jesttemptype>\", \"_id\" : \"1\" } }\n"+
                "{ \"delete\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"<jesttemptype>\", \"_id\" : \"2\" } }\n"+
                "{ \"delete\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"<jesttemptype>\", \"_id\" : \"3\" } }\n";
        assertEquals(data,delete.getData());
        assertTrue(delete.isDefaultIndexEnabled());
        assertTrue(delete.isDefaultTypeEnabled());
    }

    @Test
    public void createDocList(){
        Doc doc1 = new Doc("<jesttempindex>","<jesttemptype>","1");
        Doc doc2 = new Doc("<jesttempindex>","<jesttemptype>","2");
        Doc doc3 = new Doc("<jesttempindex>","<jesttemptype>","3");
        List<Doc> docList = new ArrayList<Doc>();
        docList.add(doc1);
        docList.add(doc2);
        docList.add(doc3);
        List<Doc> actualList = new Delete().createDocList(new String[]{"1","2","3"});
        assertEquals(docList.size(),actualList.size());
        for(int i =0;i<docList.size();i++){
            assertEquals(docList.get(i).getIndex(),actualList.get(i).getIndex());
            assertEquals(docList.get(i).getType(),actualList.get(i).getType());
            assertEquals(docList.get(i).getId(),actualList.get(i).getId());
        }
    }

    @Test
    public void prepareBulkForMultipleDelete(){
        String expected = "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\" } }\n" +
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"2\" } }\n" +
                "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"3\" } }\n" ;
        Doc doc1 = new Doc("twitter","tweet","1");
        Doc doc2 = new Doc("twitter","tweet","2");
        Doc doc3 = new Doc("twitter","tweet","3");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        assertEquals(expected,new Delete().prepareBulkForDelete(docs));
    }

    @Test
    public void prepareBulkForOneDelete(){
        String expected = "{ \"delete\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\", \"_id\" : \"1\" } }\n";
        Doc doc1 = new Doc("twitter","tweet","1");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        assertEquals(expected,new Delete().prepareBulkForDelete(docs));

    }


    class TestSource{
        String tweet;
        TestSource(String value){
            tweet = value;
        }
    }


}
