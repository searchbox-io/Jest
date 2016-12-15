package io.searchbox.core;

import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */
public class MultiSearchTest {

    @Test
    public void singleMultiSearchWithoutIndex() throws JSONException {
        String expectedData = " {\"index\" : \"_all\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void singleMultiSearchWitIndex() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}")
                .addIndex("twitter")
                .build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void multiSearchWitIndex() throws JSONException {
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
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
    }

    @Test
    public void multiSearchWithExtraParameters() throws JSONException {
        String expectedData = " {\"index\" : \"twitter\", \"search_type\" : \"query_then_fetch\", \"routing\" : \"testRoute\", \"ignore_unavailable\" : \"true\", \"allow_no_indices\" : \"true\", \"expand_wildcards\" : \"true\"}\n" +
                "{\"query\" : {\"match_all\" : {}}}\n";
        Search search = new Search.Builder("{\"query\" : {\"match_all\" : {}}}")
                .addIndex("twitter")
                .setParameter(Parameters.ROUTING, "testRoute")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setParameter(Parameters.IGNORE_UNAVAILABLE, true)
                .setParameter(Parameters.ALLOW_NO_INDICES, true)
                .setParameter("expand_wildcards", true)
                .build();
        Search search2 = new Search.Builder("{\"query\" : {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();

        assertEquals("POST", multiSearch.getRestMethodName());
        assertEquals("/_msearch", multiSearch.getURI());
        JSONAssert.assertEquals(expectedData, multiSearch.getData(null).toString(), false);
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
