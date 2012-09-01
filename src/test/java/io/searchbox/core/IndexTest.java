package io.searchbox.core;

import io.searchbox.Parameters;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class IndexTest {

    @Test
    public void indexDocument() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1", index.getURI());
        assertFalse(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithVersionParameter() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        index.addParameter(Parameters.VERSION,3);
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1?version=3", index.getURI());
        assertFalse(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithoutId() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("twitter/tweet", index.getURI());
        assertFalse(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndex() {
        Index index =  new Index.Builder(new Object()).type("tweet").id("1").build();
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("/tweet/1", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexWithoutId() {
        Index index =  new Index.Builder(new Object()).type("tweet").build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("/tweet", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertFalse(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndType() {
        Index index = new Index.Builder(new Object()).id("1").build();
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("/1", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertTrue(index.isDefaultTypeEnabled());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndTypeWithoutId() {
        Index index = new Index.Builder(new Object()).build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertTrue(index.isDefaultTypeEnabled());
    }

    @Test
    public void createSourceWithMap() {
        Map source = new HashMap();
        source.put("field","value");
        Index index = new Index.Builder(source).build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("", index.getURI());
        assertTrue(index.isDefaultIndexEnabled());
        assertTrue(index.isDefaultTypeEnabled());
        assertEquals(source,index.getData());
    }
}
