package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class SearchIntegrationTest extends AbstractIntegrationTest{

    String query = "{\n" +
            "    \"query\": {\n" +
            "        \"filtered\" : {\n" +
            "            \"query\" : {\n" +
            "                \"query_string\" : {\n" +
            "                    \"query\" : \"some query string here\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"filter\" : {\n" +
            "                \"term\" : { \"user\" : \"kimchy\" }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";
    
    @Test
    public void searchWithValidQuery() {
           try {
            ElasticSearchResult result = client.execute(new Search(query));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }


    @Test
    public void searchWithValidBoolQuery() {
        try {
            Search search = new Search(query);
            search.addIndex("cvbank");
            search.addType("candidate");
            ElasticSearchResult result = client.execute(search);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            List<Object> resultList = result.getSourceAsObjectList(Object.class);
            assertEquals(1,resultList);
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }


}
