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
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FIELDS).setIndex(INDEX).build();
        assertEquals("POST", fieldCapabilities.getRestMethodName());
        assertEquals(INDEX + "/_field_caps", fieldCapabilities.getURI(ElasticsearchVersion.UNKNOWN));
        assertEquals("{\"fields\":[\"" + TEST_FIELD + "\"]}", fieldCapabilities.getData(new Gson()));
    }

    @Test
    public void testBasicUriGenerationNoIndex() {
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FIELDS).build();
        assertEquals("POST", fieldCapabilities.getRestMethodName());
        assertEquals("_field_caps", fieldCapabilities.getURI(ElasticsearchVersion.UNKNOWN));
        assertEquals("{\"fields\":[\"" + TEST_FIELD + "\"]}", fieldCapabilities.getData(new Gson()));
    }

    @Test
    public void testBasicUriGenerationWithLevel() {
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FIELDS).setIndex(INDEX).setLevel("indices").build();
        assertEquals("POST", fieldCapabilities.getRestMethodName());
        assertEquals(INDEX + "/_field_caps?level=indices", fieldCapabilities.getURI(ElasticsearchVersion.UNKNOWN));
        assertEquals("{\"fields\":[\"" + TEST_FIELD + "\"]}", fieldCapabilities.getData(new Gson()));
    }
}
