package io.searchbox.core;

import com.google.gson.GsonBuilder;

import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.UpdateByQueryResult;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * @author Lior Knaany
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class UpdateByQueryIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "twitter";
    private static final String TYPE = "tweet";

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        final ArrayList<Class<? extends Plugin>> plugins = new ArrayList<>(super.nodePlugins());
        plugins.add(ReindexPlugin.class);
        return plugins;
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        final Settings.Builder builder = Settings.builder().put(super.nodeSettings(nodeOrdinal));
        return builder
                .put(super.nodeSettings(nodeOrdinal))
                .put("script.inline", "true")
                .build();
    }

    @Test
    public void update() throws IOException, InterruptedException {

        // create a tweet
        assertTrue(index(INDEX, TYPE, "1", "{\"user\":\"lior\",\"num\":1}").getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(index(INDEX, TYPE, "2", "{\"user\":\"kimchy\",\"num\":2}").getResult().equals(DocWriteResponse.Result.CREATED));

       refresh();
       ensureSearchable(INDEX);

        // run the search and update
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("user", "lior"));
        final String script = "ctx._source.user = ctx._source.user + '_updated';";

        final String payload = jsonBuilder()
                .startObject()
                .field("query", queryBuilder)
                .startObject("script")
                .field("inline", script)
                .endObject()
                .endObject().string();

        UpdateByQuery updateByQuery = new UpdateByQuery.Builder(payload)
                .addIndex(INDEX)
                .addType(TYPE)
                .build();

        UpdateByQueryResult result = client.execute(updateByQuery);

        // Checks
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        assertFalse(result.didTimeOut());
        assertEquals(0, result.getConflictsCount());
        assertTrue(result.getMillisTaken() > 0);
        assertEquals(1, result.getUpdatedCount());
        assertEquals(0, result.getRetryCount());
        assertEquals(0, result.getBulkRetryCount());
        assertEquals(0, result.getSearchRetryCount());
        assertEquals(0, result.getNoopCount());
        assertEquals(0, result.getFailures().size());
    }

}
