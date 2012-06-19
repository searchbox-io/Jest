package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class SearchTest {

    Search search = new Search();

    @Test
    public void addValidIndex() {
        search.addIndex("twitter");
        assertTrue(search.isIndexExist("twitter"));
    }

    @Test
    public void addEmptyIndex() {
        search.addIndex("");
        assertFalse(search.isIndexExist(""));
        assertEquals(0, search.indexSize());
    }

    @Test
    public void addValidType() {
        search.addType("tweet");
        assertTrue(search.isTypeExist("tweet"));
    }

    @Test
    public void addEmptyType() {
        search.addType("");
        assertFalse(search.isTypeExist(""));
        assertEquals(0, search.typeSize());
    }

    @Test
    public void addValidIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        search.addIndex(indexList);
        assertEquals(3, search.indexSize());
        for (String index : indexList) {
            assertTrue(search.isIndexExist(index));
        }
    }

    @Test
    public void addEmptyIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        search.addIndex(indexList);
        assertEquals(0, search.indexSize());
    }


    @Test
    public void addDuplicatedIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        indexList.add("JEST");
        indexList.add("JEST");
        search.addIndex(indexList);
        assertEquals(3, search.indexSize());
        for (String index : indexList) {
            assertTrue(search.isIndexExist(index));
        }
    }

    @Test
    public void addValidTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("java");
        search.addType(typeList);
        assertEquals(3, search.typeSize());
        for (String index : typeList) {
            assertTrue(search.isTypeExist(index));
        }
    }

    @Test
    public void addEmptyTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        search.addType(typeList);
        assertEquals(0, search.typeSize());
    }


    @Test
    public void addDuplicatedTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("Java");
        typeList.add("Java");
        typeList.add("Java");
        search.addType(typeList);
        assertEquals(3, search.typeSize());
        for (String index : typeList) {
            assertTrue(search.isTypeExist(index));
        }
    }

    @Test
    public void clearAllType() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("Java");
        typeList.add("c");
        typeList.add("groovy");
        search.addType(typeList);
        assertEquals(5, search.typeSize());
        search.clearAllType();
        assertEquals(0, search.typeSize());
    }

    @Test
    public void clearAllIndex() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        indexList.add("groovy");
        indexList.add("java");
        search.addIndex(indexList);
        assertEquals(5, search.indexSize());
        search.clearAllIndex();
        assertEquals(0, search.indexSize());
    }

    @Test
    public void removeType() {
        search.addType("tweet");
        assertTrue(search.isTypeExist("tweet"));
        assertEquals(1, search.typeSize());
        search.removeType("tweet");
        assertEquals(0, search.typeSize());
    }

    @Test
    public void removeIndex() {
        search.addIndex("twitter");
        assertTrue(search.isIndexExist("twitter"));
        assertEquals(1, search.indexSize());
        search.removeIndex("twitter");
        assertEquals(0, search.indexSize());
    }

    @Test
    public void getURIWithoutIndexAndType() {
        assertEquals("_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        search.addIndex("twitter");
        assertEquals("twitter/_search", search.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        search.addIndex("twitter");
        search.addType("tweet");
        assertEquals("twitter/tweet/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyOneType() {
        search.addType("tweet");
        assertEquals("_all/tweet/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        search.addIndex("twitter");
        search.addIndex("searchbox");
        assertEquals("twitter,searchbox/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        search.addType("tweet");
        search.addType("jest");
        assertEquals("_all/tweet,jest/_search", search.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        search.addIndex("twitter");
        search.addIndex("searchbox");
        search.addType("tweet");
        search.addType("jest");
        assertEquals("twitter,searchbox/tweet,jest/_search", search.getURI());
    }

    @Test
    public void createQueryString() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("twitter");
        set.add("searchbox");
        assertEquals("twitter,searchbox",search.createQueryString(set));
    }


}
