package io.searchbox.core;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */
public class MultiSearchTest {

    @Test
    public void singleMultiSearchWithoutIndex() {
        String expectedData = " {\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        assertEquals(expectedData.trim(), multiSearch.getData(null).toString().trim());
    }

    @Test
    public void singleMultiSearchWitIndex() {
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}")
                .addIndex("twitter")
                .build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        assertEquals(expectedData.trim(), multiSearch.getData(null).toString().trim());
    }

    @Test
    public void multiSearchWitIndex() {
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n" +
                "{\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        assertEquals(expectedData.trim(), multiSearch.getData(null).toString().trim());
    }

    @Test
    public void equals() {
        Search search1 = new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

        MultiSearch multiSearch1 = new MultiSearch.Builder(Arrays.asList(search1, search2)).build();
        MultiSearch multiSearch1Duplicate = new MultiSearch.Builder(Arrays.asList(search1, search2)).build();

        assertEquals(multiSearch1, multiSearch1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSearches() {
        Search search1 = new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

        MultiSearch multiSearch1 = new MultiSearch.Builder(search1).build();
        MultiSearch multiSearch1Duplicate = new MultiSearch.Builder(search2).build();

        assertNotEquals(multiSearch1, multiSearch1Duplicate);
    }

}
