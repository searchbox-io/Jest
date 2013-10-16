package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class ExplainTest {

    @Test
    public void explain() {
        Explain explain = new Explain.Builder("query").index("twitter").type("tweet").id("1").build();
        assertEquals("GET", explain.getRestMethodName());
        assertEquals("twitter/tweet/1/_explain", explain.getURI());
        assertEquals("query", explain.getData(null));
    }
}
