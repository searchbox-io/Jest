package io.searchbox.cluster;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PendingClusterTasksTest {
    @Test
    public void testUriGeneration() {
        PendingClusterTasks action = new PendingClusterTasks.Builder().build();
        assertEquals("/_cluster/pending_tasks", action.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
