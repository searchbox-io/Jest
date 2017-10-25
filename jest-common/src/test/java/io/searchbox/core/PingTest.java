package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PingTest {
    @Test
    public void testBasicUriGeneration() {
        Ping ping = new Ping.Builder().build();

        assertEquals("GET", ping.getRestMethodName());
        assertNull(ping.getData(null));
        assertEquals("", ping.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
