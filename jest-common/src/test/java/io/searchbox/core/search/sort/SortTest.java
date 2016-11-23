package io.searchbox.core.search.sort;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
    public void testJsonSerializationForSimpleFieldSort() {
        String expectedJson = "{\"my_field\":{}}";
        Sort s = new Sort("my_field");
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationForAscOrder() {
        String expectedJson = "{\"my_field\":{\"order\":\"asc\"}}";
        Sort s = new Sort("my_field", Sort.Sorting.ASC);
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationForDescOrder() {
        String expectedJson = "{\"my_field\":{\"order\":\"desc\"}}";
        Sort s = new Sort("my_field", Sorting.DESC);
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationForMissingValueFirst() {
        String expectedJson = "{\"my_field\":{\"missing\":\"_first\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(Missing.FIRST);
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationForMissingValueLast() {
        String expectedJson = "{\"my_field\":{\"missing\":\"_last\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(Missing.LAST);
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationForMissingValueString() {
        String expectedJson = "{\"my_field\":{\"missing\":\"***\"}}";
        Sort s = new Sort("my_field");
        s.setMissing("***");
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationForMissingValueInteger() {
        String expectedJson = "{\"my_field\":{\"missing\":\"-1\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(-1);
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationWithOrderAndMissingValue() {
        String expectedJson = "{\"my_field\":{\"order\":\"desc\",\"missing\":\"-1\"}}";
        Sort s = new Sort("my_field", Sorting.DESC);
        s.setMissing(-1);
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

    @Test
    public void testJsonSerializationWithUnmappedValue() {
        String expectedJson = "{\"my_field\":{\"ignore_unmapped\":true}}";
        Sort s = new Sort("my_field");
        s.setIgnoreUnmapped();
        JsonObject actualJsonObject = s.toJsonObject();

        assertEquals(expectedJson, new Gson().toJson(actualJsonObject));
    }

}
