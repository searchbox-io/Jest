package io.searchbox.indices;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OpenIndexTest {

    @Test
    public void testBasicUriGeneration() {
        OpenIndex openIndex = new OpenIndex.Builder("twitter").build();

        assertEquals("POST", openIndex.getRestMethodName());
        assertEquals("twitter/_open", openIndex.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        OpenIndex openIndex1 = new OpenIndex.Builder("twitter").build();
        OpenIndex openIndex1Duplicate = new OpenIndex.Builder("twitter").build();

        assertEquals(openIndex1, openIndex1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        OpenIndex openIndex1 = new OpenIndex.Builder("twitter").build();
        OpenIndex openIndex2 = new OpenIndex.Builder("myspace").build();

        assertNotEquals(openIndex1, openIndex2);
    }

}