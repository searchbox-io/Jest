package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class MultiSearchTest {

    @Test
    public void emptyMultiSearch() {
        MultiSearch multiSearch = new MultiSearch();
        executeAsserts(multiSearch);
        assertEquals("", multiSearch.getData());
    }

    @Test
    public void singleMultiSearchWithoutIndex() {
        MultiSearch multiSearch = new MultiSearch();
        Search search = new Search.Builder("{\"match_all\" : {}}").build();
        multiSearch.addSearch(search);
        executeAsserts(multiSearch);
        String expectedData = " {\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        assertEquals(expectedData.trim(), multiSearch.getData().toString().trim());
    }

    @Test
    public void singleMultiSearchWitIndex() {
        MultiSearch multiSearch = new MultiSearch();
        Search search = (Search) new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        multiSearch.addSearch(search);
        executeAsserts(multiSearch);
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        assertEquals(expectedData.trim(), multiSearch.getData().toString().trim());
    }

    @Test
    public void MultiSearchWitIndex() {
        MultiSearch multiSearch = new MultiSearch();
        Search search = (Search) new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        multiSearch.addSearch(search);

        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();
        multiSearch.addSearch(search2);

        executeAsserts(multiSearch);
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n" +
                "{\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        assertEquals(expectedData.trim(), multiSearch.getData().toString().trim());
    }

    private void executeAsserts(MultiSearch multiSearch) {
        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
    }
}
