package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */
public class MultiSearchTest {

    @Test
    public void singleMultiSearchWithoutIndex() {
        Search search = new Search.Builder("{\"match_all\" : {}}").build();
        MultiSearch multiSearch = new MultiSearch.Builder(search).build();
        executeAsserts(multiSearch);
        String expectedData = " {\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        assertEquals(expectedData.trim(), multiSearch.getData(null).toString().trim());
    }

    @Test
    public void singleMultiSearchWitIndex() {
        Search search = (Search) new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        MultiSearch multiSearch = new MultiSearch.Builder(search).build();
        executeAsserts(multiSearch);
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        assertEquals(expectedData.trim(), multiSearch.getData(null).toString().trim());
    }

    @Test
    public void MultiSearchWitIndex() {
        Search search = (Search) new Search.Builder("{\"match_all\" : {}}")
                .addIndex("twitter")
                .build();
        Search search2 = new Search.Builder("{\"match_all\" : {}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();

        executeAsserts(multiSearch);
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n" +
                "{\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        assertEquals(expectedData.trim(), multiSearch.getData(null).toString().trim());
    }

    private void executeAsserts(MultiSearch multiSearch) {
        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
    }
}
