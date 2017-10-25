package io.searchbox.indices;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RefreshTest {

    @Test
    public void testBasicUriGeneration() {
        Refresh refresh = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();

        assertEquals("POST", refresh.getRestMethodName());
        assertEquals("twitter%2Cmyspace/_refresh", refresh.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndices() {
        Refresh refresh1 = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();
        Refresh refresh1Duplicate = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();

        assertEquals(refresh1, refresh1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndices() {
        Refresh refresh1 = new Refresh.Builder().addIndex("twitter").addIndex("myspace").build();
        Refresh refresh2 = new Refresh.Builder().addIndex("twitter").addIndex("facebook").build();

        assertNotEquals(refresh1, refresh2);
    }

}