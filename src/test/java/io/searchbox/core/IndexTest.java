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
    }

    @Test
    public void indexDocumentWithVersionParameter() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").id("1").build();
        index.addParameter(Parameters.VERSION,3);
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("twitter/tweet/1?version=3", index.getURI());
    }

    @Test
    public void indexDocumentWithoutId() {
        Index index = new Index.Builder(new Object()).index("twitter").type("tweet").build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("twitter/tweet", index.getURI());
    }

    @Test
    public void indexDocumentWithDefaultIndex() {
        Index index =  new Index.Builder(new Object()).type("tweet").id("1").build();
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("<jesttempindex>/tweet/1", index.getURI());
    }

    @Test
    public void indexDocumentWithDefaultIndexWithoutId() {
        Index index =  new Index.Builder(new Object()).type("tweet").build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("<jesttempindex>/tweet", index.getURI());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndType() {
        Index index = new Index.Builder(new Object()).id("1").build();
        assertEquals("PUT", index.getRestMethodName());
        assertEquals("<jesttempindex>/<jesttemptype>/1", index.getURI());
    }

    @Test
    public void indexDocumentWithDefaultIndexAndTypeWithoutId() {
        Index index = new Index.Builder(new Object()).build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("<jesttempindex>/<jesttemptype>", index.getURI());
    }

    @Test
    public void createSourceWithMap() {
        Map source = new HashMap();
        source.put("field","value");
        Index index = new Index.Builder(source).build();
        assertEquals("POST", index.getRestMethodName());
        assertEquals("<jesttempindex>/<jesttemptype>", index.getURI());
        assertEquals(source,index.getData());
    }
}
