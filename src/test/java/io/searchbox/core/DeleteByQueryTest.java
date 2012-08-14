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


public class DeleteByQueryTest {

    DeleteByQuery deleteByQuery = new DeleteByQuery();

    @Test
    public void addValidIndex() {
        deleteByQuery.addIndex("twitter");
        assertTrue(deleteByQuery.isIndexExist("twitter"));
    }

    @Test
    public void addEmptyIndex() {
        deleteByQuery.addIndex("");
        assertFalse(deleteByQuery.isIndexExist(""));
        assertEquals(0, deleteByQuery.indexSize());
    }

    @Test
    public void addValidType() {
        deleteByQuery.addType("tweet");
        assertTrue(deleteByQuery.isTypeExist("tweet"));
    }

    @Test
    public void addEmptyType() {
        deleteByQuery.addType("");
        assertFalse(deleteByQuery.isTypeExist(""));
        assertEquals(0, deleteByQuery.typeSize());
    }

    @Test
    public void addValidIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        deleteByQuery.addIndex(indexList);
        assertEquals(3, deleteByQuery.indexSize());
        for (String index : indexList) {
            assertTrue(deleteByQuery.isIndexExist(index));
        }
    }

    @Test
    public void addEmptyIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        deleteByQuery.addIndex(indexList);
        assertEquals(0, deleteByQuery.indexSize());
    }

    @Test
    public void addDuplicatedIndexCollection() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        indexList.add("JEST");
        indexList.add("JEST");
        deleteByQuery.addIndex(indexList);
        assertEquals(3, deleteByQuery.indexSize());
        for (String index : indexList) {
            assertTrue(deleteByQuery.isIndexExist(index));
        }
    }

    @Test
    public void addValidTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("java");
        deleteByQuery.addType(typeList);
        assertEquals(3, deleteByQuery.typeSize());
        for (String index : typeList) {
            assertTrue(deleteByQuery.isTypeExist(index));
        }
    }

    @Test
    public void addEmptyTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        deleteByQuery.addType(typeList);
        assertEquals(0, deleteByQuery.typeSize());
    }

    @Test
    public void addDuplicatedTypeCollection() {
        List<String> typeList = new ArrayList<String>();
        typeList.add("tweet");
        typeList.add("io");
        typeList.add("Java");
        typeList.add("Java");
        typeList.add("Java");
        deleteByQuery.addType(typeList);
        assertEquals(3, deleteByQuery.typeSize());
        for (String index : typeList) {
            assertTrue(deleteByQuery.isTypeExist(index));
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
        deleteByQuery.addType(typeList);
        assertEquals(5, deleteByQuery.typeSize());
        deleteByQuery.clearAllType();
        assertEquals(0, deleteByQuery.typeSize());
    }

    @Test
    public void clearAllIndex() {
        List<String> indexList = new ArrayList<String>();
        indexList.add("twitter");
        indexList.add("searchbox");
        indexList.add("JEST");
        indexList.add("groovy");
        indexList.add("java");
        deleteByQuery.addIndex(indexList);
        assertEquals(5, deleteByQuery.indexSize());
        deleteByQuery.clearAllIndex();
        assertEquals(0, deleteByQuery.indexSize());
    }

    @Test
    public void removeType() {
        deleteByQuery.addType("tweet");
        assertTrue(deleteByQuery.isTypeExist("tweet"));
        assertEquals(1, deleteByQuery.typeSize());
        deleteByQuery.removeType("tweet");
        assertEquals(0, deleteByQuery.typeSize());
    }

    @Test
    public void removeIndex() {
        deleteByQuery.addIndex("twitter");
        assertTrue(deleteByQuery.isIndexExist("twitter"));
        assertEquals(1, deleteByQuery.indexSize());
        deleteByQuery.removeIndex("twitter");
        assertEquals(0, deleteByQuery.indexSize());
    }

    @Test
    public void getURIWithoutIndexAndType() {
        assertEquals("_all/_query", deleteByQuery.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        deleteByQuery.addIndex("twitter");
        assertEquals("twitter/_query", deleteByQuery.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        deleteByQuery.addType("tweet");
        deleteByQuery.addType("jest");
        assertEquals("_all/_query", deleteByQuery.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        deleteByQuery.addIndex("twitter");
        deleteByQuery.addType("tweet");
        assertEquals("twitter/tweet/_query", deleteByQuery.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        deleteByQuery.addIndex("twitter");
        deleteByQuery.addIndex("searchbox");
        assertEquals("twitter,searchbox/_query", deleteByQuery.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        deleteByQuery.addIndex("twitter");
        deleteByQuery.addIndex("searchbox");
        deleteByQuery.addType("tweet");
        deleteByQuery.addType("jest");
        assertEquals("twitter,searchbox/tweet,jest/_query", deleteByQuery.getURI());
    }

    @Test
    public void createQueryString() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("twitter");
        set.add("searchbox");
        assertEquals("twitter,searchbox",deleteByQuery.createQueryString(set));
    }

}
