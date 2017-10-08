package io.searchbox.client.config;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testDefaultMaxIdleConnectionTime() {
        ClientConfig config = new ClientConfig.Builder("someUri").multiThreaded(true).build();

        assertEquals(-1L, config.getMaxConnectionIdleTime());
        assertEquals(TimeUnit.SECONDS, config.getMaxConnectionIdleTimeDurationTimeUnit());
    }

    @Test
    public void testCustomMaxIdleConnectionTime() {
        ClientConfig config = new ClientConfig.Builder("someUri").multiThreaded(true)
                                                                 .maxConnectionIdleTime(30L, TimeUnit.MINUTES)
                                                                 .build();
        assertEquals(30L, config.getMaxConnectionIdleTime());
        assertEquals(TimeUnit.MINUTES, config.getMaxConnectionIdleTimeDurationTimeUnit());
    }
}
