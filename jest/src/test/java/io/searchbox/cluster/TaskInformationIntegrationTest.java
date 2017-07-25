package io.searchbox.cluster;

import com.google.common.collect.ImmutableMap;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.reindex.Reindex;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class TaskInformationIntegrationTest extends AbstractIntegrationTest {

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        final ArrayList<Class<? extends Plugin>> plugins = new ArrayList<>(super.nodePlugins());
        plugins.add(ReindexPlugin.class);
        return plugins;
    }

    @Test
    public void shouldReturnTaskInformation() throws IOException {

        String sourceIndex = "test_source_index";
        String destIndex = "test_dest_index";
        String documentType = "test_type";
        String documentId = "test_id";

        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{}");
        flushAndRefresh(sourceIndex);

        ImmutableMap<String, Object> source = ImmutableMap.of("index", sourceIndex);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).refresh(true).waitForCompletion(false).build();
        JestResult result = client.execute(reindex);

        String task = (String) result.getValue("task");
        assertNotNull(task);
        JestResult taskInformation = client.execute(new TasksInformation.Builder().task(task).build());
        Boolean completed = (Boolean) taskInformation.getValue("completed");
        assertNotNull(completed);
    }
}
