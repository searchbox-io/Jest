package io.searchbox.core.search.sort;

import com.google.gson.Gson;
import io.searchbox.core.search.sort.Sort.Missing;
import io.searchbox.core.search.sort.Sort.Sorting;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Riccardo Tasso
 * @author cihat keser
 */
public class SortTest {

    @Test
    public void testJsonSerializationForSimpleFieldSort() {
        String expectedJson = "{\"my_field\":{}}";
        Sort s = new Sort("my_field");
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationForAscOrder() {
        String expectedJson = "{\"my_field\":{\"order\":\"asc\"}}";
        Sort s = new Sort("my_field", Sort.Sorting.ASC);
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationForDescOrder() {
        String expectedJson = "{\"my_field\":{\"order\":\"desc\"}}";
        Sort s = new Sort("my_field", Sorting.DESC);
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationForMissingValueFirst() {
        String expectedJson = "{\"my_field\":{\"missing\":\"_first\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(Missing.FIRST);
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationForMissingValueLast() {
        String expectedJson = "{\"my_field\":{\"missing\":\"_last\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(Missing.LAST);
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationForMissingValueString() {
        String expectedJson = "{\"my_field\":{\"missing\":\"***\"}}";
        Sort s = new Sort("my_field");
        s.setMissing("***");
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationForMissingValueInteger() {
        String expectedJson = "{\"my_field\":{\"missing\":\"-1\"}}";
        Sort s = new Sort("my_field");
        s.setMissing(-1);
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationWithOrderAndMissingValue() {
        String expectedJson = "{\"my_field\":{\"missing\":\"-1\",\"order\":\"desc\"}}";
        Sort s = new Sort("my_field", Sorting.DESC);
        s.setMissing(-1);
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

    @Test
    public void testJsonSerializationWithUnmappedValue() {
        String expectedJson = "{\"my_field\":{\"ignore_unmapped\":true}}";
        Sort s = new Sort("my_field");
        s.setIgnoreUnmapped();
        Map actualMap = s.toMap();

        assertEquals(expectedJson, new Gson().toJson(actualMap));
    }

}
