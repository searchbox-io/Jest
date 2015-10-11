package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
public class MultiSearchIntegrationTest extends AbstractIntegrationTest {

    String query = "{" +
            "    \"query\": {" +
            "        \"filtered\" : {" +
            "            \"query\" : {" +
            "                \"query_string\" : {" +
            "                    \"query\" : \"newman\"" +
            "                }" +
            "            }," +
            "            \"filter\" : {" +
            "                \"term\" : { \"user\" : \"kramer\" }" +
            "            }" +
            "        }" +
            "    }" +
            "}";

    @Test
    public void singleSearch() throws IOException {
        String index = "ms_test_ix";
        createIndex(index);
        index(index, "mytype", "1", "{\"user\":\"kramer\", \"content\":\"newman\"}");
        index(index, "mytype", "2", "{\"user\":\"kramer\", \"content\":\"newman jerry\"}");
        index(index, "mytype", "3", "{\"user\":\"kramer\", \"content\":\"george\"}");
        ensureSearchable(index);

        Search search = new Search.Builder(query).build();
        MultiSearch multiSearch = new MultiSearch.Builder(search).build();
        JestResult result = client.execute(multiSearch);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void singleMultiSearchWitIndex() throws IOException {
        Search search = new Search.Builder("{\"query\": {\"match_all\" : {}}}").addIndex("twitter").build();
        MultiSearch multiSearch = new MultiSearch.Builder(search).build();
        JestResult result = client.execute(multiSearch);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void multiSearchWithIndex() throws IOException {
        Search search = new Search.Builder("{\"query\": {\"match_all\" : {}}}").addIndex("twitter").build();
        Search search2 = new Search.Builder("{\"query\": {\"match_all\" : {}}}").build();

        MultiSearch multiSearch = new MultiSearch.Builder(search).addSearch(search2).build();
        JestResult result = client.execute(multiSearch);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}
