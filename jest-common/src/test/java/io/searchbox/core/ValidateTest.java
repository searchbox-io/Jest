package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */


public class ValidateTest {

    @Test
    public void validateQuery() {
        Validate validate = new Validate.Builder("{query:query}").build();

        assertEquals("POST", validate.getRestMethodName());
        assertEquals("{query:query}", validate.getData(null));
        assertEquals("/_validate/query", validate.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void validateQueryWithIndex() {
        Validate validate = new Validate.Builder("{query:query}").index("twitter").build();

        assertEquals("POST", validate.getRestMethodName());
        assertEquals("{query:query}", validate.getData(null));
        assertEquals("twitter/_validate/query", validate.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void validateQueryWithIndexAndType() {
        Validate validate = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();

        assertEquals("POST", validate.getRestMethodName());
        assertEquals("{query:query}", validate.getData(null));
        assertEquals("twitter/tweet/_validate/query", validate.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameQuery() {
        Validate validate1 = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        Validate validate1Duplicate = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();

        assertEquals(validate1, validate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Validate validate1 = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        Validate validate2 = new Validate.Builder("{query2:query2}").index("twitter").type("tweet").build();

        assertNotEquals(validate1, validate2);
    }

}
