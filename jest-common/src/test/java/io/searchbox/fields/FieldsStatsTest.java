package io.searchbox.fields;

import com.google.gson.Gson;
import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FieldsStatsTest {
    static final String TEST_FIELD = "test_name";
    static final String INDEX = "twitter";
    static final List FIELDS = Collections.singletonList(TEST_FIELD);


    @Test
    public void testBasicUriGeneration() {
        FieldStats fieldStats = new FieldStats.Builder(FIELDS).setIndex(INDEX).build();
        assertEquals("POST", fieldStats.getRestMethodName());
        assertEquals(INDEX + "/_field_stats", fieldStats.getURI(ElasticsearchVersion.V55));
        assertEquals("{\"fields\":[\"" + TEST_FIELD + "\"]}", fieldStats.getData(new Gson()));
    }

    @Test
    public void testBasicUriGenerationNoIndex() {
        FieldStats fieldStats = new FieldStats.Builder(FIELDS).build();
        assertEquals("POST", fieldStats.getRestMethodName());
        assertEquals("_field_stats", fieldStats.getURI(ElasticsearchVersion.V55));
        assertEquals("{\"fields\":[\"" + TEST_FIELD + "\"]}", fieldStats.getData(new Gson()));
    }

    @Test
    public void testBasicUriGenerationWithLevel() {
        FieldStats fieldStats = new FieldStats.Builder(FIELDS).setIndex(INDEX).setLevel("indices").build();
        assertEquals("POST", fieldStats.getRestMethodName());
        assertEquals(INDEX + "/_field_stats?level=indices", fieldStats.getURI(ElasticsearchVersion.V55));
        assertEquals("{\"fields\":[\"" + TEST_FIELD + "\"]}", fieldStats.getData(new Gson()));
    }
}
