package io.searchbox.indices.settings;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GetSettingsTest {

    @Test
    public void testDefaultUriGeneration() {
        String expectedUri = "_all/_settings";

        GetSettings getSettings = new GetSettings.Builder().build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testDefaultUriGenerationWithPrefix() {
        String expectedUri = "_all/_settings?prefix=index.routing.allocation.";

        GetSettings getSettings = new GetSettings.Builder().prefixQuery("index.routing.allocation.").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testDefaultUriGenerationWithEmptyPrefix() {
        String expectedUri = "_all/_settings?prefix=";

        GetSettings getSettings = new GetSettings.Builder().prefixQuery("").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testSingleIndexUriGeneration() {
        String expectedUri = "books/_settings";

        GetSettings getSettings = new GetSettings.Builder().addIndex("books").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testSingleIndexUriGenerationWithPrefix() {
        String expectedUri = "books/_settings?prefix=index.routing.allocation.";

        GetSettings getSettings = new GetSettings.Builder().addIndex("books").prefixQuery("index.routing.allocation.").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testMultipleIndicesUriGeneration() {
        String expectedUri = "books%2Carticles/_settings";

        GetSettings getSettings = new GetSettings.Builder().addIndex("books").addIndex("articles").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testMultipleIndicesUriGenerationWithPrefix() {
        String expectedUri = "books%2Carticles/_settings?prefix=index.routing.allocation.";

        GetSettings getSettings = new GetSettings.Builder()
                .addIndex("books").addIndex("articles").prefixQuery("index.routing.allocation.").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testWildcardUriGeneration() {
        String expectedUri = "2013-*/_settings";

        GetSettings getSettings = new GetSettings.Builder().addIndex("2013-*").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testWildcardUriGenerationWithPrefix() {
        String expectedUri = "2013-*/_settings?prefix=index.routing.allocation.";

        GetSettings getSettings = new GetSettings.Builder().addIndex("2013-*").prefixQuery("index.routing.allocation.").build();
        assertEquals(expectedUri, getSettings.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        GetSettings getSettings1 = new GetSettings.Builder().addIndex("2013-*").build();
        GetSettings getSettings1Duplicate = new GetSettings.Builder().addIndex("2013-*").build();

        assertEquals(getSettings1, getSettings1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        GetSettings getSettings1 = new GetSettings.Builder().addIndex("2013-*").build();
        GetSettings getSettings2 = new GetSettings.Builder().addIndex("2015-*").build();

        assertNotEquals(getSettings1, getSettings2);
    }

}