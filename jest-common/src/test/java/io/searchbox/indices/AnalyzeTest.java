package io.searchbox.indices;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

    @Test
    public void testPayloadWithASingleTextEntry() throws Exception {
        Analyze analyze = new Analyze.Builder()
                .text("foo")
                .build();
        assertEquals("{\"text\": [\"foo\"]}", analyze.getData(new Gson()));
    }

    @Test
    public void testPayloadWithAMultipleTextEntry() throws Exception {
        Analyze analyze = new Analyze.Builder()
                .text("foo")
                .text("bar")
                .build();
        assertEquals("{\"text\": [\"foo\",\"bar\"]}", analyze.getData(new Gson()));
    }

    @Test
    public void testPayloadWithAListTextEntry() throws Exception {
        Analyze analyze = new Analyze.Builder()
                .text(ImmutableList.of("foo", "bar"))
                .text("baz")
                .build();
        assertEquals("{\"text\": [\"foo\",\"bar\",\"baz\"]}", analyze.getData(new Gson()));
    }

    @Test
    public void equalsReturnsTrueForSameSource() {
        Analyze analyze1 = new Analyze.Builder()
                .index("test")
                .analyzer("whitespace")
                .text("source")
                .build();
        Analyze analyze1Duplicate = new Analyze.Builder()
                .index("test")
                .analyzer("whitespace")
                .text("source")
                .build();

        assertEquals(analyze1, analyze1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSources() {
        Analyze analyze1 = new Analyze.Builder()
                .index("test")
                .analyzer("whitespace")
                .text("source")
                .build();
        Analyze analyze2 = new Analyze.Builder()
                .index("test")
                .analyzer("whitespace")
                .text("source2")
                .build();

        assertNotEquals(analyze1, analyze2);
    }

}
