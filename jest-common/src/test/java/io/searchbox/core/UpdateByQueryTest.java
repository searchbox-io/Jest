package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Lior Knaany
 */
public class UpdateByQueryTest {

    @Test
    public void getURIWithoutIndexAndType() {
        assertEquals("_all/_update_by_query", new UpdateByQuery.Builder(null).build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        assertEquals("twitter/_update_by_query", new UpdateByQuery.Builder(null).addIndex("twitter").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        assertEquals("_all/tweet%2Cjest/_update_by_query", new UpdateByQuery.Builder(null).addType("tweet").addType("jest").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        assertEquals("twitter/tweet/_update_by_query", new UpdateByQuery.Builder(null).addIndex("twitter").addType("tweet").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        assertEquals("twitter%2Csearchbox/_update_by_query",
                new UpdateByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        assertEquals("twitter%2Csearchbox/tweet%2Cjest/_update_by_query", new UpdateByQuery.Builder(null)
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equals() {
        UpdateByQuery deleteUserKramer = new UpdateByQuery.Builder("{\"user\":\"kramer\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        UpdateByQuery deleteUserKramerDuplicate = new UpdateByQuery.Builder("{\"user\":\"kramer\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();

        assertEquals(deleteUserKramer, deleteUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        UpdateByQuery deleteUserKramer = new UpdateByQuery.Builder("{\"user\":\"kramer\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        UpdateByQuery deleteUserJerry = new UpdateByQuery.Builder("{\"user\":\"jerry\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();

        assertNotEquals(deleteUserKramer, deleteUserJerry);
    }

}
