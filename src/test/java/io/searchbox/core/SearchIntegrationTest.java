package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.Parameters;
import io.searchbox.client.JestResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class SearchIntegrationTest extends AbstractIntegrationTest {

    String query = "{\n" +
            "    \"query\": {\n" +
            "        \"filtered\" : {\n" +
            "            \"query\" : {\n" +
            "                \"query_string\" : {\n" +
            "                    \"query\" : \"test\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"filter\" : {\n" +
            "                \"term\" : { \"user\" : \"kimchy\" }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithValidQuery() {
        try {
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            JestResult result = client.execute(new Search(query));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "cvbank")
    public void searchWithQueryBuilder() {
        try {
            Index index = new Index.Builder("{\"user\":\"kimchy\"}").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            JestResult result = client.execute(new Search(QueryBuilders.queryString("kimchy")));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void searchWithValidTermQuery() {
        try {

            Index index = new Index.Builder("{\"user\":\"kimchy\", \"content\":\"That is test\"}").index("twitter").type("tweet").build();
            index.addParameter(Parameters.REFRESH, true);
            client.execute(index);

            Search search = new Search(query);
            search.addIndex("twitter");
            search.addType("tweet");
            JestResult result = client.execute(search);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            List<Object> resultList = result.getSourceAsObjectList(Object.class);
            assertEquals(1, resultList.size());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }
}
