package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */
public class DeleteByQueryTest {

    @Test
    public void getURIWithoutIndexAndType() {
        assertEquals("_all/_delete_by_query", new DeleteByQuery.Builder(null).build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        assertEquals("twitter/_delete_by_query", new DeleteByQuery.Builder(null).addIndex("twitter").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        assertEquals("_all/tweet%2Cjest/_delete_by_query", new DeleteByQuery.Builder(null).addType("tweet").addType("jest").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        assertEquals("twitter/tweet/_delete_by_query", new DeleteByQuery.Builder(null).addIndex("twitter").addType("tweet").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        assertEquals("twitter%2Csearchbox/_delete_by_query",
                new DeleteByQuery.Builder(null).addIndex("twitter").addIndex("searchbox").build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        assertEquals("twitter%2Csearchbox/tweet%2Cjest/_delete_by_query", new DeleteByQuery.Builder(null)
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build().getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equals() {
        DeleteByQuery deleteUserKramer = new DeleteByQuery.Builder("{\"user\":\"kramer\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        DeleteByQuery deleteUserKramerDuplicate = new DeleteByQuery.Builder("{\"user\":\"kramer\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();

        assertEquals(deleteUserKramer, deleteUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        DeleteByQuery deleteUserKramer = new DeleteByQuery.Builder("{\"user\":\"kramer\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        DeleteByQuery deleteUserJerry = new DeleteByQuery.Builder("{\"user\":\"jerry\"}")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();

        assertNotEquals(deleteUserKramer, deleteUserJerry);
    }

}
