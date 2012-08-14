package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */


public class CountTest {
    Count count = new Count();

    @Test
    public void addValidIndex() {
        count.addIndex("twitter");
        assertTrue(count.isIndexExist("twitter"));
    }

    @Test
    public void addEmptyIndex() {
        count.addIndex("");
        assertFalse(count.isIndexExist(""));
        assertEquals(0, count.indexSize());
    }

    @Test
    public void addValidType() {
        count.addType("tweet");
        assertTrue(count.isTypeExist("tweet"));
    }

    @Test
    public void addEmptyType() {
        count.addType("");
        assertFalse(count.isTypeExist(""));
        assertEquals(0, count.typeSize());
    }

    @Test
    public void addValidIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        count.addIndex(indexList);
        assertEquals(3, count.indexSize());
        for (String index : indexList) {
            assertTrue(count.isIndexExist(index));
        }
    }

    @Test
    public void addEmptyIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        count.addIndex(indexList);
        assertEquals(0, count.indexSize());
    }

    @Test
    public void addDuplicatedIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        indexList.add("JEST");
        indexList.add("JEST");
        count.addIndex(indexList);
        assertEquals(3, count.indexSize());
        for (String index : indexList) {
            assertTrue(count.isIndexExist(index));
        }
    }

    @Test
    public void addValidTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("java");
        count.addType(typeList);
        assertEquals(3, count.typeSize());
        for (String index : typeList) {
            assertTrue(count.isTypeExist(index));
        }
    }

    @Test
    public void addEmptyTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        count.addType(typeList);
        assertEquals(0, count.typeSize());
    }

    @Test
    public void addDuplicatedTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("Java");
        typeList.add("Java");
        typeList.add("Java");
        count.addType(typeList);
        assertEquals(3, count.typeSize());
        for (String index : typeList) {
            assertTrue(count.isTypeExist(index));
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
        count.addType(typeList);
        assertEquals(5, count.typeSize());
        count.clearAllType();
        assertEquals(0, count.typeSize());
    }

    @Test
    public void clearAllIndex() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        indexList.add("groovy");
        indexList.add("java");
        count.addIndex(indexList);
        assertEquals(5, count.indexSize());
        count.clearAllIndex();
        assertEquals(0, count.indexSize());
    }

    @Test
    public void removeType() {
        count.addType("tweet");
        assertTrue(count.isTypeExist("tweet"));
        assertEquals(1, count.typeSize());
        count.removeType("tweet");
        assertEquals(0, count.typeSize());
    }

    @Test
    public void removeIndex() {
        count.addIndex("twitter");
        assertTrue(count.isIndexExist("twitter"));
        assertEquals(1, count.indexSize());
        count.removeIndex("twitter");
        assertEquals(0, count.indexSize());
    }

    @Test
    public void getURIWithoutIndexAndType() {
        assertEquals("_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        count.addIndex("twitter");
        assertEquals("twitter/_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        count.addType("tweet");
        count.addType("jest");
        assertEquals("_count", count.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        count.addIndex("twitter");
        count.addType("tweet");
        assertEquals("twitter/tweet/_count", count.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        count.addIndex("twitter");
        count.addIndex("searchbox");
        assertEquals("twitter,searchbox/_count", count.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        count.addIndex("twitter");
        count.addIndex("searchbox");
        count.addType("tweet");
        count.addType("jest");
        assertEquals("twitter,searchbox/tweet,jest/_count", count.getURI());
    }

    @Test
    public void createQueryString() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("twitter");
        set.add("searchbox");
        assertEquals("twitter,searchbox",count.createQueryString(set));
    }

}
