package io.searchbox.core;

import fr.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import fr.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class BulkIntegrationTest extends AbstractIntegrationTest{

    @Test
    public void bulkOperationWithIndex(){

    }
}
