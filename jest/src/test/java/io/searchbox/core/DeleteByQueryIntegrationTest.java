package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Dogukan Sonmez
 */
//@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class DeleteByQueryIntegrationTest extends AbstractIntegrationTest {

//    @Override
//    protected Collection<Class<? extends Plugin>> nodePlugins() {
//        return Collections.singletonList(DeleteByQueryPlugin.class);
//    }

    //@Test
    public void delete() throws IOException, InterruptedException {
        final String index = "twitter";
        final String type = "tweet";
        final String id = "1";
        final String query = "{\n" +
                "    \"query\": {\n" +
                "        \"term\": { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";

        assertTrue(index(index, type, id, "{\"user\":\"kimchy\"}").getResult().equals(DocWriteResponse.Result.CREATED));
        refresh();
        ensureSearchable(index);

        DeleteByQuery deleteByQuery = new DeleteByQuery.Builder(query)
                .addIndex("twitter")
                .addType("tweet")
                .build();

        JestResult result = client.execute(deleteByQuery);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertEquals(
                0,
                result.getJsonObject().getAsJsonObject("_shards").get("failed").getAsInt()
        );
        assertEquals(
                1,
                result.getJsonObject().getAsJsonObject("_shards").get("successful").getAsInt()
        );
    }

}
