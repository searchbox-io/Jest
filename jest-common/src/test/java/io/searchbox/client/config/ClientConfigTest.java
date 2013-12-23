package io.searchbox.client.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Min Cha
 */
public class ClientConfigTest {

    @Test
    public void testTimeoutSettings() {
        ClientConfig config = new ClientConfig.Builder("someUri").connTimeout(1500).readTimeout(2000).build();

        assertEquals(1500, config.getConnTimeout());
        assertEquals(2000, config.getReadTimeout());
    }

    @Test
    public void testTimeoutSettingsAsDefault() {
        ClientConfig config = new ClientConfig.Builder("someUri").multiThreaded(true).build();

        assertTrue(config.getConnTimeout() > 0);
        assertTrue(config.getReadTimeout() > 0);
    }
}
