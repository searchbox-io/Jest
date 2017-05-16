package io.searchbox.indices.aliases;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AliasExistsTest {
    @Test
    public void testBasicUriGeneration() {
        AliasExists aliasExists = new AliasExists.Builder().build();

        assertEquals("HEAD", aliasExists.getRestMethodName());
        assertEquals("_all/_alias/*", aliasExists.getURI());
    }

    @Test
    public void testBasicUriGenerationWithIndex() {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("indexName").build();

        assertEquals("HEAD", aliasExists.getRestMethodName());
        assertEquals("indexName/_alias/*", aliasExists.getURI());
    }

    @Test
    public void testBasicUriGenerationWithAlias() {
        AliasExists aliasExists = new AliasExists.Builder().alias("aliasName").build();

        assertEquals("HEAD", aliasExists.getRestMethodName());
        assertEquals("_all/_alias/aliasName", aliasExists.getURI());
    }

    @Test
    public void testBasicUriGenerationWithAliasAndIndex() {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("indexName").alias("aliasName").build();

        assertEquals("HEAD", aliasExists.getRestMethodName());
        assertEquals("indexName/_alias/aliasName", aliasExists.getURI());
    }
}