package io.searchbox.indices;


import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.template.GetTemplate;
import io.searchbox.indices.template.PutTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author asierdelpozo
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class PutTemplateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testPutTemplate() {
        PutTemplate putTemplate = new PutTemplate.Builder("new_template_1",
                "{	" +
                        "\"template\" : \"*\"," +
                        "\"order\" : 0," +
                        "\"settings\" : {" +
                        "	\"number_of_shards\" : 1" +
                        "}," +
                        "\"mappings\" : {" +
                        "	\"type1\" : {" +
                        "		\"_source\" : { \"enabled\" : false }" +
                        "	}" +
                        "}" +
                        "}")
                .build();
        try {
            JestResult result = client.execute(putTemplate);
            assertNotNull(result);
            assertTrue(result.isSucceeded());

            GetTemplate getTemplate = new GetTemplate.Builder("new_template_1").build();
            result = client.execute(getTemplate);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }

}
