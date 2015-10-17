package io.searchbox.core;

import com.google.common.base.Predicate;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class DeleteByQueryIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void delete() throws IOException, InterruptedException {
        final String query = "{\n" +
                "    \"query\": {\n" +
                "        \"term\": { \"user\" : \"kimchy\" }\n" +
                "    }\n" +
                "}";
        client.execute(new Index.Builder("{\"user\":\"kimchy\"}").index("twitter").type("tweet").id("1").build());

        waitForDocumentToBeIndexed(query);

        DeleteByQuery deleteByQuery = new DeleteByQuery.Builder(query)
                .addIndex("twitter")
                .addType("tweet")
                .build();

        JestResult result = client.execute(deleteByQuery);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertEquals(
                0,
                result.getJsonObject().getAsJsonObject("_indices").getAsJsonObject("twitter").get("failed").getAsInt()
        );
        assertEquals(
                1,
                result.getJsonObject().getAsJsonObject("_indices").getAsJsonObject("twitter").get("deleted").getAsInt()
        );
    }

    private void waitForDocumentToBeIndexed(final String query) throws InterruptedException {
        awaitBusy(new Predicate<Object>() {
            @Override
            public boolean apply(Object input) {
                try {
                    SearchResult searchResult = client.execute(new Search.Builder(query)
                            .addIndex("twitter")
                            .addType("tweet")
                            .build());
                    return null != searchResult.getFirstHit(Map.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
