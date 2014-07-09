package io.searchbox.core;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Dogukan Sonmez
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class IndexIntegrationTest extends AbstractIntegrationTest {

    public static final String INDEX_NAME = "twitter";
    public static final String TYPE_NAME = "tweet";

    Map<Object, Object> source = new HashMap<Object, Object>();

    @Test
    public void indexDocumentWithValidParametersAndWithoutSettings() throws IOException {
        source.put("user", "searchbox");
        JestResult result = client.execute(new Index.Builder(source).index(INDEX_NAME).type(TYPE_NAME).id("1").build());
        executeTestCase(result);
    }

    @Test
    public void automaticIdGeneration() throws IOException {
        source.put("user", "jest");
        JestResult result = client.execute(new Index.Builder(source).index(INDEX_NAME).type(TYPE_NAME).build());
        executeTestCase(result);
    }

    @Test
    public void indexAsynchronously() throws InterruptedException, ExecutionException, IOException {
        source.put("user", "jest");
        Action action = new Index.Builder(source).index(INDEX_NAME).type(TYPE_NAME).build();

        client.executeAsync(action, new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                executeTestCase(result);
            }

            @Override
            public void failed(Exception ex) {
                fail("Failed during the running asynchronous call");
            }

        });

        //wait for asynchronous call
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldIndexDateFieldsWithoutFormatConversion() throws Exception {
        putMappingAndCheckSuccess();
        source.put("user", "searchbox");
        source.put("creationDate", new Date());
        JestResult result = client.execute(new Index.Builder(source).index(INDEX_NAME).type(TYPE_NAME).id("1").build());
        executeTestCase(result);
    }

    private void putMappingAndCheckSuccess() throws IOException {
        createIndex(INDEX_NAME);
        PutMapping putMapping = new PutMapping.Builder(INDEX_NAME, TYPE_NAME,
                "{ \"" + TYPE_NAME + "\" : { \"properties\" : { \"creationDate\" : {\"type\" : \"date\"} } } }"
        ).build();
        JestResult result = client.execute(putMapping);
        assertNotNull("Can't put mapping", result);
        assertTrue(result.isSucceeded());
    }

    private void executeTestCase(JestResult result) {
        assertNotNull(result);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
}
