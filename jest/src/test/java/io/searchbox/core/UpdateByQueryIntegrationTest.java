package io.searchbox.core;

import com.google.gson.GsonBuilder;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.groovy.GroovyPlugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
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
        // ReindexPlugin.class is needed in order to make the _update_by_query REST action available in the embedded Elasticsearch test server
        // GroovyPlugin is needed for running the update script
        return pluginList(GroovyPlugin.class, ReindexPlugin.class);
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.settingsBuilder()
                .put(super.nodeSettings(nodeOrdinal))
                .put("script.inline", "on")
                .put("script.indexed", "on")
                .put("plugin.types", ReindexPlugin.class.getName())
                .build();
    }

    @Test
    public void update() throws IOException, InterruptedException {

        // create a tweet
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"lior\",\"num\":1}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy\",\"num\":2}").refresh(true)).actionGet().isCreated());

        // run the search and update
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("user", "lior"));
        final String script = "ctx._source.user = ctx._source.user + '_updated';";

        final String payload =  jsonBuilder()
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

        System.out.println("uri: " + updateByQuery.getURI());
        System.out.println("+query: " + updateByQuery.getData(new GsonBuilder().setPrettyPrinting().create()));

        JestResult result = client.execute(updateByQuery);

        // Checks
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        System.out.println("+jsonobj: " + result.getJsonObject());

        assertEquals(0, result.getJsonObject().get("failures").getAsJsonArray().size());
        assertEquals(1, result.getJsonObject().get("updated").getAsInt());
    }

}
