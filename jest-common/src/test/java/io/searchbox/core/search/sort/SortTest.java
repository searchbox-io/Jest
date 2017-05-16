package io.searchbox.core.search.sort;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.searchbox.core.search.sort.Sort.Missing;
import io.searchbox.core.search.sort.Sort.Sorting;

/**
 * @author Riccardo Tasso
 * @author cihat keser
 */
public class SortTest {

    @Test
    public void testJsonSerializationForSimpleFieldSort() throws JSONException {
        String expectedJson = "{\"my_field\":{}}";
        Sort s = new Sort("my_field");
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationForAscOrder() throws JSONException {
        String expectedJson = "{\"my_field\":{\"order\":\"asc\"}}";
        Sort s = new Sort("my_field", Sort.Sorting.ASC);
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationForDescOrder() throws JSONException {
        String expectedJson = "{\"my_field\":{\"order\":\"desc\"}}";
        Sort s = new Sort("my_field", Sorting.DESC);
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationForMissingValueFirst() throws JSONException {
        String expectedJson = "{\"my_field\":{\"missing\":\"_first\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(Missing.FIRST);
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationForMissingValueLast() throws JSONException {
        String expectedJson = "{\"my_field\":{\"missing\":\"_last\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(Missing.LAST);
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationForMissingValueString() throws JSONException {
        String expectedJson = "{\"my_field\":{\"missing\":\"***\"}}";
        Sort s = new Sort("my_field");
        s.setMissing("***");
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationForMissingValueInteger() throws JSONException {
        String expectedJson = "{\"my_field\":{\"missing\":\"-1\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(-1);
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationWithOrderAndMissingValue() throws JSONException {
        String expectedJson = "{\"my_field\":{\"order\":\"desc\",\"missing\":\"-1\"}}";
        Sort s = new Sort("my_field", Sorting.DESC);
        s.setMissing(-1);
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationWithUnmappedValue() throws JSONException {
        String expectedJson = "{\"my_field\":{\"ignore_unmapped\":true}}";
        Sort s = new Sort("my_field");
        s.setIgnoreUnmapped();
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }

    @Test
    public void testJsonSerializationWithUnmappedType() throws JSONException {
        String expectedJson = "{\"my_field\":{\"unmapped_type\":\"long\"}}";
        Sort s = new Sort("my_field");
        s.setUnmappedType("long");
        JsonObject actualJsonObject = s.toJsonObject();

        JSONAssert.assertEquals(expectedJson, new Gson().toJson(actualJsonObject), false);
    }
}
