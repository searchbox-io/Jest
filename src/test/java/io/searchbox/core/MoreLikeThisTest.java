package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Dogukan Sonmez
 */


public class MoreLikeThisTest {

    @Test
    public void moreLikeThis(){
        MoreLikeThis moreLikeThis = new MoreLikeThis.Builder("1").query("query").index("twitter").type("tweet").build();
        assertEquals("POST",moreLikeThis.getRestMethodName());
        assertEquals("twitter/tweet/1/_mlt",moreLikeThis.getURI());
        assertEquals("MORELIKETHIS",moreLikeThis.getName());
        assertEquals("query",moreLikeThis.getData());
    }

    @Test
    public void moreLikeThisWithoutQuery(){
        MoreLikeThis moreLikeThis = new MoreLikeThis.Builder("1").index("twitter").type("tweet").build();
        assertEquals("GET",moreLikeThis.getRestMethodName());
        assertEquals("twitter/tweet/1/_mlt",moreLikeThis.getURI());
        assertEquals("MORELIKETHIS",moreLikeThis.getName());
        assertNull(moreLikeThis.getData());
    }
}
