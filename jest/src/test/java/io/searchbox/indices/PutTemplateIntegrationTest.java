package io.searchbox.indices;


import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.template.GetTemplate;
import io.searchbox.indices.template.PutTemplate;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;

/**
 * @author asierdelpozo
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class PutTemplateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testPutTemplate() throws IOException {
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

        JestResult result = client.execute(putTemplate);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetTemplate getTemplate = new GetTemplate.Builder("new_template_1").build();
        result = client.execute(getTemplate);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

}
