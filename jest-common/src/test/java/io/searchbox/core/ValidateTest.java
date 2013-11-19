package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class ValidateTest {

    @Test
    public void validateQuery() {
        Validate validate = new Validate.Builder("{query:query}").build();
        executeAsserts(validate);
        assertEquals("/_validate/query", validate.getURI());
    }

    @Test
    public void validateQueryWithIndex() {
        Validate validate = new Validate.Builder("{query:query}").index("twitter").build();
        executeAsserts(validate);
        assertEquals("twitter/_validate/query", validate.getURI());
    }

    @Test
    public void validateQueryWithIndexAndType() {
        Validate validate = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        executeAsserts(validate);
        assertEquals("twitter/tweet/_validate/query", validate.getURI());

    }

    private void executeAsserts(Validate validate) {
        assertEquals("POST", validate.getRestMethodName());
        assertEquals("{query:query}", validate.getData(null));
    }

}
