package io.searchbox.indices.settings;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UpdateSettingsTest {

    @Test
    public void testDefaultBehaviour() {
        String expectedUri = "_all/_settings";

        UpdateSettings updateSettings = new UpdateSettings.Builder("").build();
        assertEquals(expectedUri, updateSettings.getURI(ElasticsearchVersion.UNKNOWN));
        assertEquals("", updateSettings.getData(null));
        assertEquals("PUT", updateSettings.getRestMethodName());
    }

    @Test
    public void equalsReturnsTrueForSameSource() {
        UpdateSettings updateSettings1 = new UpdateSettings.Builder("source 1").build();
        UpdateSettings updateSettings1Duplicate = new UpdateSettings.Builder("source 1").build();

        assertEquals(updateSettings1, updateSettings1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSource() {
        UpdateSettings updateSettings1 = new UpdateSettings.Builder("source 1").build();
        UpdateSettings updateSettings2 = new UpdateSettings.Builder("source 2").build();

        assertNotEquals(updateSettings1, updateSettings2);
    }

}
