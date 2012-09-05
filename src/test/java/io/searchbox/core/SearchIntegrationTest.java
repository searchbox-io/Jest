package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.*;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class SearchIntegrationTest extends AbstractIntegrationTest{

    @Test
    public void searchWithValidQuery() {
        QueryBuilder query = boolQuery()
                .must(termQuery("content", "test1"))
                .must(termQuery("content", "test4"))
                .mustNot(termQuery("content", "test2"))
                .should(termQuery("content", "test3"));

        try {
            ElasticSearchResult result = client.execute(new Search(query.toString()));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }


    @Test
    public void searchWithValidBoolQuery() {
        QueryBuilder query = boolQuery()
                .must(termQuery("content", "Jest"));

        try {
            Search search = new Search(query.toString());
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
