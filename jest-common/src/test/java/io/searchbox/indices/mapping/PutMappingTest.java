package io.searchbox.indices.mapping;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PutMappingTest {

    @Test
    public void testBasicUriGeneration() {
        PutMapping putMapping = new PutMapping.Builder("twitter", "tweet", "source").build();

        assertEquals("PUT", putMapping.getRestMethodName());
        assertEquals("twitter/tweet/_mapping", putMapping.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameSource() {
        PutMapping putMapping1 = new PutMapping.Builder("twitter", "tweet", "source").build();
        PutMapping putMapping1Duplicate = new PutMapping.Builder("twitter", "tweet", "source").build();

        assertEquals(putMapping1, putMapping1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSource() {
        PutMapping putMapping1 = new PutMapping.Builder("twitter", "tweet", "source 1").build();
        PutMapping putMapping2 = new PutMapping.Builder("twitter", "tweet", "source 2").build();

        assertNotEquals(putMapping1, putMapping2);
    }

}