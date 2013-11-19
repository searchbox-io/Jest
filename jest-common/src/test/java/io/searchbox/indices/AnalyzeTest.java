package io.searchbox.indices;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class AnalyzeTest {

    @Test
    public void testBasicUrlGeneration() {
        Analyze analyze = new Analyze.Builder()
                .analyzer("standard")
                .build();
        assertEquals("/_analyze?analyzer=standard", analyze.getURI());
    }

    @Test
    public void testUrlGenerationWithCustomTransientAnalyzer() {
        Analyze analyze = new Analyze.Builder()
                .tokenizer("keyword")
                .filter("lowercase")
                .build();
        assertEquals("/_analyze?tokenizer=keyword&filters=lowercase", analyze.getURI());
    }

    @Test
    public void testUrlGenerationWithIndex() {
        Analyze analyze = new Analyze.Builder()
                .index("test")
                .build();
        assertEquals("test/_analyze", analyze.getURI());
    }

    @Test
    public void testUrlGenerationWithIndexAndAnalyzer() {
        Analyze analyze = new Analyze.Builder()
                .index("test")
                .analyzer("whitespace")
                .build();
        assertEquals("test/_analyze?analyzer=whitespace", analyze.getURI());
    }

    @Test
    public void testUrlGenerationWithIndexAndFieldMapping() {
        Analyze analyze = new Analyze.Builder()
                .index("test")
                .field("obj1.field1")
                .build();
        assertEquals("test/_analyze?field=obj1.field1", analyze.getURI());
    }

}
