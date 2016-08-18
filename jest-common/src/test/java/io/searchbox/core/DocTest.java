package io.searchbox.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DocTest {

    @Test
    public void testToMapWithOnlyRequiredParameters() {
        String index = "idx0";
        String type = "typo";
        String id = "00001_AE";
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        expectedMap.put("_index", index);
        expectedMap.put("_type", type);
        expectedMap.put("_id", id);

        Doc doc = new Doc(index, type, id);
        Map<String, Object> actualMap = doc.toMap();

        assertEquals(3, actualMap.size());
        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void testToMapWithFieldsParameter() {
        String index = "idx0";
        String type = "typo";
        String id = "00001_AE";
        List<String> fields = Arrays.asList("user", "location");

        Doc doc = new Doc(index, type, id);
        doc.addFields(fields);
        Map<String, Object> actualMap = doc.toMap();

        assertEquals(4, actualMap.size());
        assertEquals(index, actualMap.get("_index"));
        assertEquals(type, actualMap.get("_type"));
        assertEquals(id, actualMap.get("_id"));
        assertEquals(fields, actualMap.get("fields"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionWithNullIndex() {
        new Doc(null, "type", "id");
        fail("Constructor should have thrown an exception when index was null");
    }

    @Test
    public void testConstructionWithNullType() {
        String index = "idx0";
        String id = "00001_AE";
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        expectedMap.put("_index", index);
        expectedMap.put("_id", id);

        Doc doc = new Doc(index, id);
        Map<String, Object> actualMap = doc.toMap();

        assertEquals(2, actualMap.size());
        assertEquals(expectedMap, actualMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionWithNullId() {
        new Doc("idx", "type", null);
        fail("Constructor should have thrown an exception when id was null");
    }

}