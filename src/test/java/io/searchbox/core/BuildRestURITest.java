package io.searchbox.core;

import io.searchbox.Index;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class BuildRestURITest {
    Delete delete;

    @Before
    public void setUp() throws Exception {
        delete = new Delete(new Index("test"));
    }

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "http://localhost:9200/twitter/tweet/1";
        String actual = delete.buildURI(new Index("twitter", "tweet", "1"));
        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithUValidParameters() {
        delete.buildURI(new Index("", "^^^^^^^", ""));
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithNullParameters() {
        delete.buildURI(null);
    }


}