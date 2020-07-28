package io.searchbox.core;

import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class MultiSearchIntegrationTest extends AbstractIntegrationTest {

    String query = "{ \"query\": { \"bool\": { \"should\": [ { \"query_string\": { \"query\": \"newman\" } } ], \"filter\": { \"term\": { \"user\": \"kramer\" } }, \"minimum_should_match\": 1 } } }";

    @Test
    public void multipleSearchRequests() throws IOException {
        String index = "ms_test_ix";
        createIndex(index);
        index(index, "mytype", "1", "{\"user\":\"kramer\", \"content\":\"newman\"}");
        index(index, "mytype", "2", "{\"user\":\"kramer\", \"content\":\"newman jerry\"}");
        index(index, "mytype", "3", "{\"user\":\"kramer\", \"content\":\"george\"}");
        refresh();
        ensureSearchable(index);

        Search complexSearch = new Search.Builder(query).build();
        Search simpleSearch = new Search.Builder("{\"query\": {\"match_all\" : {}}}").addIndex(index).build();

        MultiSearch multiSearch = new MultiSearch.Builder(Arrays.asList(complexSearch, simpleSearch)).build();
        MultiSearchResult result = client.execute(multiSearch);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<MultiSearchResult.MultiSearchResponse> responses = result.getResponses();
        assertEquals(2, responses.size());

        MultiSearchResult.MultiSearchResponse complexSearchResponse = responses.get(0);
        assertFalse(complexSearchResponse.isError);
        assertNull(complexSearchResponse.errorMessage);
        SearchResult complexSearchResult = complexSearchResponse.searchResult;
        assertTrue(complexSearchResult.isSucceeded());
        assertNull(complexSearchResult.getErrorMessage());
        assertEquals(Long.valueOf(2), complexSearchResult.getTotal());
        List<SearchResult.Hit<Comment, Void>> complexSearchHits = complexSearchResult.getHits(Comment.class);
        assertEquals(2, complexSearchHits.size());

        MultiSearchResult.MultiSearchResponse simpleSearchResponse = responses.get(1);
        assertFalse(simpleSearchResponse.isError);
        assertNull(simpleSearchResponse.errorMessage);
        SearchResult simpleSearchResult = simpleSearchResponse.searchResult;
        assertTrue(simpleSearchResult.isSucceeded());
        assertNull(simpleSearchResult.getErrorMessage());
        assertEquals(Long.valueOf(3), simpleSearchResult.getTotal());
        List<SearchResult.Hit<Comment, Void>> simpleSearchHits = simpleSearchResult.getHits(Comment.class);
        assertEquals(3, simpleSearchHits.size());
    }

    @Test
    public void multipleSearchRequestsWithOneFaulty() throws IOException {
        String index = "ms_test_ix";
        createIndex(index);
        index(index, "mytype", "1", "{\"user\":\"kramer\", \"content\":\"newman\"}");
        index(index, "mytype", "2", "{\"user\":\"kramer\", \"content\":\"newman jerry\"}");
        index(index, "mytype", "3", "{\"user\":\"kramer\", \"content\":\"george\"}");
        refresh();
        ensureSearchable(index);

        Search complexSearch = new Search.Builder(query).build();
        Search simpleSearch = new Search.Builder("{\"query\": {\"match_all\" : {}}}").addIndex(index).build();
        Search faultySearch = new Search.Builder("{\"query\": {\"match_all\" : {}}}").addIndex("not-found").build();

        MultiSearch multiSearch = new MultiSearch.Builder(Arrays.asList(complexSearch, simpleSearch, faultySearch)).build();
        MultiSearchResult result = client.execute(multiSearch);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<MultiSearchResult.MultiSearchResponse> responses = result.getResponses();
        assertEquals(3, responses.size());

        MultiSearchResult.MultiSearchResponse complexSearchResponse = responses.get(0);
        assertFalse(complexSearchResponse.isError);
        assertNull(complexSearchResponse.errorMessage);
        SearchResult complexSearchResult = complexSearchResponse.searchResult;
        assertNotNull(complexSearchResult);
        assertTrue(complexSearchResult.isSucceeded());
        assertNull(complexSearchResult.getErrorMessage());
        assertEquals(Long.valueOf(2L), complexSearchResult.getTotal());
        List<SearchResult.Hit<Comment, Void>> complexSearchHits = complexSearchResult.getHits(Comment.class);
        assertEquals(2, complexSearchHits.size());

        MultiSearchResult.MultiSearchResponse simpleSearchResponse = responses.get(1);
        assertFalse(simpleSearchResponse.isError);
        assertNull(simpleSearchResponse.errorMessage);
        SearchResult simpleSearchResult = simpleSearchResponse.searchResult;
        assertNotNull(simpleSearchResult);
        assertTrue(simpleSearchResult.isSucceeded());
        assertNull(simpleSearchResult.getErrorMessage());
        assertEquals(Long.valueOf(3L), simpleSearchResult.getTotal());
        List<SearchResult.Hit<Comment, Void>> simpleSearchHits = simpleSearchResult.getHits(Comment.class);
        assertEquals(3, simpleSearchHits.size());

        MultiSearchResult.MultiSearchResponse faultySearchResponse = responses.get(2);
        assertTrue(faultySearchResponse.isError);
        assertNotNull(faultySearchResponse.errorMessage);
        assertNull(faultySearchResponse.searchResult);
    }

    public class Comment {
        public String user;
        public String content;
    }

}
