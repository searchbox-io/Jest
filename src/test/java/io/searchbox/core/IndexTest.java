package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class IndexTest {

    @Test
    public void indexDocument() {
        Index index = new Index("twitter", "tweet", "1",new Object());
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1", index.getURI());
        assertFalse(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithoutId() {
        Index index = new Index("twitter", "tweet",new Object());
        assertEquals("POST", index.getRestMethodName());
        assertEquals("twitter/tweet", index.getURI());
        assertFalse(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWitMultipleSource() {
        Index index = new Index("twitter", "tweet",createTestSource());
        assertEquals("POST", index.getRestMethodName());
        assertEquals("_bulk", index.getURI());
        String expectedData = "{ \"index\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\"}}\n" +
                "{\"field1\":\"object1\",\"field2\":\"value1\"}\n" +
                "{ \"index\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\"}}\n" +
                "{\"field1\":\"object2\",\"field2\":\"value2\"}\n" +
                "{ \"index\" : { \"_index\" : \"twitter\", \"_type\" : \"tweet\"}}\n" +
                "{\"field1\":\"object3\",\"field2\":\"value3\"}\n";
        assertEquals(expectedData,index.getData());
        assertFalse(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndex() {
        Index index = new Index("tweet",new Object(), "1");
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("/tweet/1", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexWithoutId() {
        Index index = new Index("tweet",new Object());
        assertEquals("POST", index.getRestMethodName());
        assertEquals("/tweet", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexWithMultipleSource() {
        Index index = new Index("tweet",createTestSource());
        assertEquals("POST", index.getRestMethodName());
        assertEquals("_bulk", index.getURI());
        String expectedData = "{ \"index\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"tweet\"}}\n" +
                "{\"field1\":\"object1\",\"field2\":\"value1\"}\n" +
                "{ \"index\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"tweet\"}}\n" +
                "{\"field1\":\"object2\",\"field2\":\"value2\"}\n" +
                "{ \"index\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"tweet\"}}\n" +
                "{\"field1\":\"object3\",\"field2\":\"value3\"}\n";
        assertEquals(expectedData,index.getData());
        assertTrue(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndType() {
        Index index = new Index(new Object(),"1");
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("/1", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertTrue(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndTypeWithoutId() {
        Index index = new Index(new Object());
        assertEquals("POST", index.getRestMethodName());
        assertEquals("", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertTrue(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndTypeWithMultipleSource() {
        Index index = new Index(createTestSource());
        assertEquals("POST", index.getRestMethodName());
        assertEquals("_bulk", index.getURI());
        String expectedData = "{ \"index\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"<jesttemptype>\"}}\n" +
                "{\"field1\":\"object1\",\"field2\":\"value1\"}\n" +
                "{ \"index\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"<jesttemptype>\"}}\n" +
                "{\"field1\":\"object2\",\"field2\":\"value2\"}\n" +
                "{ \"index\" : { \"_index\" : \"<jesttempindex>\", \"_type\" : \"<jesttemptype>\"}}\n" +
                "{\"field1\":\"object3\",\"field2\":\"value3\"}\n";
        assertEquals(expectedData,index.getData());
        assertTrue(index.isDefaultIndexEnabled());
        assertTrue(index.isDefaultTypeEnabled());
    }

    @Test
    public void prepareBulkForOneIndex(){
        String expected = "{ \"index\" : { \"_index\" : \"test\", \"_type\" : \"type1\"} }\n" +
                "{ \"tweet\" : \"value1\" }\n";
        TestSource source = new TestSource("value1");
        List<Object> sources = new ArrayList<Object>();
        sources.add(source);
        assertEquals(expected.replaceAll("\\s",""), ((String) new Index().prepareBulkForIndex(sources, "test", "type1")).replaceAll("\\s",""));
    }


    @Test
    public void prepareBulkForMultipleIndex(){
        String expected = "{\"index\":{\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"tweet\":\"first index\" }\n" +
                "{\"index\":{ \"_index\":\"twitter\",\"_type\":\"tweet\"}}\n" +
                "{\"tweet\":\"second index\" }\n" +
                "{\"index\":{ \"_index\":\"twitter\", \"_type\" : \"tweet\"} }\n" +
                "{\"tweet\":\"third index\"}\n";
        TestSource source1 = new TestSource("first index");
        TestSource source2 = new TestSource("second index");
        TestSource source3 = new TestSource("third index");
        List<Object> sources = new ArrayList<Object>();
        sources.add(source1);
        sources.add(source2);
        sources.add(source3);
        assertEquals(expected.replaceAll("\\s",""),((String) new Index().prepareBulkForIndex(sources,"twitter","tweet")).replaceAll("\\s",""));
    }


    class TestObject{
        String field1;
        String field2;
        TestObject(String field1, String field2){
            this.field1 = field1;
            this.field2 = field2;
        }
    }


    class TestSource{
        String tweet;
        TestSource(String value){
            tweet = value;
        }
    }


    private List<Object> createTestSource() {
        List<Object> sources = new ArrayList<Object>();
        TestObject object1 = new TestObject("object1","value1");
        TestObject object2 = new TestObject("object2","value2");
        TestObject object3 = new TestObject("object3","value3");
        sources.add(object1);
        sources.add(object2);
        sources.add(object3);
        return sources;
    }

}
