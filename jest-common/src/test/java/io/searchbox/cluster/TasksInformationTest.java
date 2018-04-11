package io.searchbox.cluster;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.*;

public class TasksInformationTest {

    @Test
    public void testUriGeneration() {
        TasksInformation action = new TasksInformation.Builder().build();
        assertEquals("_tasks", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testUriGenerationSpecificTask() {
        TasksInformation action = new TasksInformation.Builder().task("node_id:task_id").build();
        assertEquals("_tasks/node_id:task_id", action.getURI(ElasticsearchVersion.UNKNOWN));
    }
}