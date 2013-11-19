package io.searchbox.core.search.sort;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.core.search.sort.Sort.Missing;
import io.searchbox.core.search.sort.Sort.Sorting;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Riccardo Tasso
 */


public class SortTest {

    @Test
    public void simpleTest() {
        Sort s = new Sort("my_field");
        assertEquals("\"my_field\"", s.toString());
    }

    @Test
    public void complexTest() {
        Sort s = new Sort("my_field", Sort.Sorting.ASC);

        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(s.toString());

        assertTrue(parsed.getAsJsonObject().has("my_field"));
        JsonElement element = parsed.getAsJsonObject().get("my_field");

        assertTrue(element.getAsJsonObject().has("order"));
        element = element.getAsJsonObject().get("order");
        assertEquals("asc", element.getAsString());

        s = new Sort("my_field", Sort.Sorting.DESC);

        parsed = parser.parse(s.toString());

        assertTrue(parsed.getAsJsonObject().has("my_field"));
        element = parsed.getAsJsonObject().get("my_field");

        assertTrue(element.getAsJsonObject().has("order"));
        element = element.getAsJsonObject().get("order");
        assertEquals("desc", element.getAsString());
    }

    @Test
    public void missingValueTest() {

        // first

        Sort s = new Sort("my_field");
        s.setMissing(Missing.FIRST);

        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        JsonObject myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("missing"));
        assertEquals(myField.get("missing").getAsString(), "_first");

        // last

        s = new Sort("my_field");
        s.setMissing(Missing.LAST);

        parser = new JsonParser();
        parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("missing"));
        assertEquals(myField.get("missing").getAsString(), "_last");

        // value String

        s = new Sort("my_field");
        s.setMissing("***");

        parser = new JsonParser();
        parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("missing"));
        assertEquals(myField.get("missing").getAsString(), "***");

        // value Integer

        s = new Sort("my_field");
        s.setMissing(new Integer(-1));

        parser = new JsonParser();
        parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("missing"));
        assertEquals(myField.get("missing").getAsInt(), -1);

        // mixed
        s = new Sort("my_field", Sorting.DESC);
        s.setMissing(new Integer(-1));

        parser = new JsonParser();
        parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("missing"));
        assertEquals(myField.get("missing").getAsInt(), -1);
        assertTrue(myField.has("order"));
        assertEquals(myField.get("order").getAsString(), "desc");

    }

    @Test
    public void unmappedTest() {

        // simple

        Sort s = new Sort("my_field");
        s.setIgnoreUnmapped();

        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        JsonObject myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("ignore_unmapped"));
        assertTrue(myField.get("ignore_unmapped").getAsBoolean());

        // complex

        s = new Sort("my_field", Sorting.DESC);
        s.setMissing(Missing.LAST);
        s.setIgnoreUnmapped();

        parser = new JsonParser();
        parsed = parser.parse(s.toString());
        assertTrue(parsed.getAsJsonObject().has("my_field"));

        myField = parsed.getAsJsonObject().get("my_field").getAsJsonObject();
        assertTrue(myField.has("ignore_unmapped"));
        assertTrue(myField.get("ignore_unmapped").getAsBoolean());
    }

}
