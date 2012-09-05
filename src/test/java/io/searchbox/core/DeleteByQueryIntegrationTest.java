package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.ElasticSearchResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class DeleteByQueryIntegrationTest extends AbstractIntegrationTest{

    @Test
    public void searchWithValidQuery() {
        QueryBuilder query = boolQuery()
                .must(termQuery("content", "test1"))
                .must(termQuery("content", "test4"))
                .mustNot(termQuery("content", "test2"))
                .should(termQuery("content", "test3"));

        try {
            ElasticSearchResult result = client.execute(new DeleteByQuery(query.toString()));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void searchWithValidBoolQuery() {
        QueryBuilder query = boolQuery()
                .must(termQuery("content", "JEST"));

        try {
            DeleteByQuery deleteByQuery = new DeleteByQuery(query.toString());
            deleteByQuery.addIndex("cvbank");
            deleteByQuery.addType("candidate");
            ElasticSearchResult result = client.execute(deleteByQuery);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:" + e.getMessage());
        }
    }

}
