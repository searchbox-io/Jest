package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class ExplainTest {

    @Test
    public void explain() {
        Explain explain = new Explain.Builder("twitter", "tweet", "1", "query").build();
        assertEquals("GET", explain.getRestMethodName());
        assertEquals("twitter/tweet/1/_explain", explain.getURI());
        assertEquals("query", explain.getData(null));
    }
}
