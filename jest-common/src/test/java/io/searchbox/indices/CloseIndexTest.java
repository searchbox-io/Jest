package io.searchbox.indices;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author cihat keser
 */
public class CloseIndexTest {

    @Test
    public void testBasicUrlGeneration() {
        CloseIndex closeIndex = new CloseIndex.Builder("twitter").build();

        assertEquals("POST", closeIndex.getRestMethodName());
        assertEquals("twitter/_close", closeIndex.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndices() {
        CloseIndex closeIndex1 = new CloseIndex.Builder("twitter").build();
        CloseIndex closeIndex1Duplicate = new CloseIndex.Builder("twitter").build();

        assertEquals(closeIndex1, closeIndex1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndices() {
        CloseIndex closeIndex1 = new CloseIndex.Builder("twitter").build();
        CloseIndex closeIndex2 = new CloseIndex.Builder("myspace").build();

        assertNotEquals(closeIndex1, closeIndex2);
    }

}