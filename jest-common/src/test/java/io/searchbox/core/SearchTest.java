package io.searchbox.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.Action;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.core.search.sort.Sort.Sorting;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 * @author Riccardo Tasso
 * @author cihat keser
 */
public class SearchTest {

    @Test
    public void getURIWithoutIndexAndType() {
        Action search = new Search.Builder("").build();
        assertEquals("_all/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Action search = new Search.Builder("").addIndex("twitter").build();
        assertEquals("twitter/_search", search.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Action search = new Search.Builder("").addIndex("twitter").addType("tweet").build();
        assertEquals("twitter/tweet/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyOneType() {
        Action search = new Search.Builder("").addType("tweet").build();
        assertEquals("_all/tweet/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Action search = new Search.Builder("").addIndex("twitter").addIndex("searchbox").build();
        assertEquals("twitter%2Csearchbox/_search", search.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        Action search = new Search.Builder("").addType("tweet").addType("jest").build();
        assertEquals("_all/tweet%2Cjest/_search", search.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Action search = new Search.Builder("")
                .addIndex("twitter")
                .addIndex("searchbox")
                .addType("tweet")
                .addType("jest")
                .build();
        assertEquals("twitter%2Csearchbox/tweet%2Cjest/_search", search.getURI());
    }

    @Test
    public void sortTest() {
        String query = "{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }}";
        List<Sort> sorting = new ArrayList<Sort>();
        sorting.add(new Sort("population", Sorting.ASC));
        sorting.add(new Sort("population", Sorting.DESC));
        sorting.add(new Sort("population"));
        Action search = new Search.Builder(query).addSort(sorting).build();

        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(null).toString());
        JsonObject obj = parsed.getAsJsonObject();
        JsonArray sort = obj.getAsJsonArray("sort");

        assertEquals(3, sort.size());

        // sort 0
        JsonObject test = sort.get(0).getAsJsonObject();
        assertTrue(test.has("population"));

        test = test.getAsJsonObject("population");
        assertTrue(test.has("order"));
        assertEquals("asc", test.get("order").getAsString());

        // sort 1
        test = sort.get(1).getAsJsonObject();
        assertTrue(test.has("population"));

        test = test.getAsJsonObject("population");
        assertTrue(test.has("order"));
        assertEquals("desc", test.get("order").getAsString());

        // sort 2
        assertEquals("population", sort.get(2).getAsString());
    }
}
