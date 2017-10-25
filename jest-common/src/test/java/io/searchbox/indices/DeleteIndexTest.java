package io.searchbox.indices;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */
public class DeleteIndexTest {

    @Test
    public void testBasicUriGenerationWithJustIndex() {
        DeleteIndex delete = new DeleteIndex.Builder("twitter").build();

        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter", delete.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithIndexAndType() {
        DeleteIndex delete = new DeleteIndex.Builder("twitter").type("tweet").build();

        assertEquals("DELETE", delete.getRestMethodName());
        assertEquals("twitter/tweet", delete.getURI(ElasticsearchVersion.UNKNOWN));

    }

    @Test
    public void equalsReturnsTrueForSameIndexAndType() {
        DeleteIndex delete1 = new DeleteIndex.Builder("twitter").type("tweet").build();
        DeleteIndex delete1Duplicate = new DeleteIndex.Builder("twitter").type("tweet").build();

        assertEquals(delete1, delete1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndexAndType() {
        DeleteIndex delete1 = new DeleteIndex.Builder("twitter").type("tweet").build();
        DeleteIndex delete2 = new DeleteIndex.Builder("twitter2").type("tweet2").build();

        assertNotEquals(delete1, delete2);
    }

}
