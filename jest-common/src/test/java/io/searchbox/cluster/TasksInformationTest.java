package io.searchbox.cluster;

import io.searchbox.action.Action;
import org.junit.Test;

import static org.junit.Assert.*;

public class TasksInformationTest {

    @Test
    public void testUriGeneration() {
        Action action = new TasksInformation.Builder().build();
        assertEquals("_tasks", action.getURI());
    }

    @Test
    public void testUriGenerationSpecificTask() {
        Action action = new TasksInformation.Builder().task("node_id:task_id").build();
        assertEquals("_tasks/node_id:task_id", action.getURI());
    }
}