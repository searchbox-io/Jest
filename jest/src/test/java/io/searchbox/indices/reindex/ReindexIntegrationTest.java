package io.searchbox.indices.reindex;

import com.google.common.collect.ImmutableMap;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author fabien baligand
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE)
public class ReindexIntegrationTest extends AbstractIntegrationTest {

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        final ArrayList<Class<? extends Plugin>> plugins = new ArrayList<>(super.nodePlugins());
        plugins.add(ReindexPlugin.class);
        return plugins;
    }

    @Test
    public void testReindex() throws IOException, InterruptedException {
        String sourceIndex = "my_source_index";
        String destIndex = "my_dest_index";
        String documentType = "my_type";
        String documentId = "my_id";

        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{}");
        flushAndRefresh(sourceIndex);

        ImmutableMap<String, Object> source = ImmutableMap.of("index", sourceIndex);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).refresh(true).build();
        JestResult result = client.execute(reindex);

        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(indexExists(destIndex));
        assertTrue(get(destIndex, documentType, documentId).isExists());
    }
}
