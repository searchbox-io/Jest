package io.searchbox.cluster;

import io.searchbox.action.Action;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PendingClusterTasksTest {
    @Test
    public void testUriGeneration() {
        Action action = new PendingClusterTasks.Builder().build();
        assertEquals("/_cluster/pending_tasks", action.getURI());
    }
}
