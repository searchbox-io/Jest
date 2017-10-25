package io.searchbox.indices.mapping;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DeleteMappingTest {

    @Test
    public void testBasicUriGeneration() {
        DeleteMapping deleteMapping = new DeleteMapping.Builder("twitter","tweet").build();

        assertEquals("DELETE", deleteMapping.getRestMethodName());
        assertEquals("twitter/tweet/_mapping", deleteMapping.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        DeleteMapping deleteMapping1 = new DeleteMapping.Builder("twitter","tweet").build();
        DeleteMapping deleteMapping1Duplicate = new DeleteMapping.Builder("twitter","tweet").build();

        assertEquals(deleteMapping1, deleteMapping1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        DeleteMapping deleteMapping1 = new DeleteMapping.Builder("twitter","tweet").build();
        DeleteMapping deleteMapping2 = new DeleteMapping.Builder("twitter","myspace").build();

        assertNotEquals(deleteMapping1, deleteMapping2);
    }

}