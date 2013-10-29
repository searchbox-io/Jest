package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Dogukan Sonmez
 */


public class MoreLikeThisTest {

    @Test
    public void moreLikeThis() {
        MoreLikeThis moreLikeThis = new MoreLikeThis.Builder("twitter", "tweet", "1", "query").build();
        assertEquals("POST", moreLikeThis.getRestMethodName());
        assertEquals("twitter/tweet/1/_mlt", moreLikeThis.getURI());
        assertEquals("query", moreLikeThis.getData(null));
    }

    @Test
    public void moreLikeThisWithoutQuery() {
        MoreLikeThis moreLikeThis = new MoreLikeThis.Builder("twitter", "tweet", "1", null).build();
        assertEquals("GET", moreLikeThis.getRestMethodName());
        assertEquals("twitter/tweet/1/_mlt", moreLikeThis.getURI());
        assertNull(moreLikeThis.getData(null));
    }
}
