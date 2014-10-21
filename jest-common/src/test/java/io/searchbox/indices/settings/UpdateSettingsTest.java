package io.searchbox.indices.settings;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UpdateSettingsTest {

    @Test
    public void testDefaultBehaviour() {
        String expectedUri = "_all/_settings";

        UpdateSettings updateSettings = new UpdateSettings.Builder("").build();
        assertEquals(expectedUri, updateSettings.getURI());
        assertEquals("", updateSettings.getData(null));
        assertEquals("PUT", updateSettings.getRestMethodName());
    }

}
