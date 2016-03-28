package io.searchbox.core;

import com.google.gson.*;
import io.searchbox.action.Action;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.core.search.sort.Sort.Sorting;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 * @author Riccardo Tasso
 * @author cihat keser
 */
public class SearchTest {

    Sort sortByPopulationAsc = new Sort("population", Sorting.ASC);
    Sort sortByPopulationDesc = new Sort("population", Sorting.DESC);
    Sort sortByPopulation = new Sort("population");

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
    public void getURIForTemplateWithoutIndexAndType() {
        Action search = new Search.TemplateBuilder("").build();
        assertEquals("_all/_search/template", search.getURI());
    }
    
    @Test
    public void getURIForTemplateWithIndexAndType() {
        Action search = new Search.TemplateBuilder("").addIndex("twitter").addType("tweet").build();
        assertEquals("twitter/tweet/_search/template", search.getURI());
    }

    @Test
    public void sortTest() {
        String query = "{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }}";
        Action search = new Search.Builder(query)
                .addSort(Arrays.asList(sortByPopulationAsc, sortByPopulationDesc, sortByPopulation)).build();

        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(new Gson()).toString());
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
        test = sort.get(2).getAsJsonObject();
        assertTrue(test.has("population"));

        test = test.getAsJsonObject("population");
        assertFalse(test.has("order"));
        assertFalse(test.has("order"));
    }

    @Test
    public void addSortShouldNotOverrideExistingSortDefinitions() {
        String query = "{\"query\" : { \"term\" : { \"name\" : \"Milano\" } }, \"sort\": [{\"existing\": { \"order\": \"desc\" }}]}";
        Action search = new Search.Builder(query)
                .addSort(Arrays.asList(sortByPopulationAsc, sortByPopulationDesc)).build();

        JsonParser parser = new JsonParser();
        JsonElement parsed = parser.parse(search.getData(new Gson()));
        JsonObject obj = parsed.getAsJsonObject();
        JsonArray sort = obj.getAsJsonArray("sort");

        assertNotNull(sort);
        assertEquals(3, sort.size());

        assertEquals("{\"existing\":{\"order\":\"desc\"}}", sort.get(0).toString());
        assertEquals("{\"population\":{\"order\":\"asc\"}}", sort.get(1).toString());
        assertEquals("{\"population\":{\"order\":\"desc\"}}", sort.get(2).toString());
    }

    @Test
    public void equalsReturnsTrueForSameQueries() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet").build();
        Search search1Duplicate = new Search.Builder("query1").addIndex("twitter").addType("tweet").build();

        assertEquals(search1, search1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet").build();
        Search search2 = new Search.Builder("query2").addIndex("twitter").addType("tweet").build();

        assertNotEquals(search1, search2);
    }

    @Test
    public void equalsReturnsTrueForSameSortList() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet")
                .addSort(Arrays.asList(sortByPopulationAsc, sortByPopulation)).build();
        Search search1Duplicate = new Search.Builder("query1").addIndex("twitter").addType("tweet")
                .addSort(Arrays.asList(sortByPopulationAsc, sortByPopulation)).build();

        assertEquals(search1, search1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentSortList() {
        Search search1 = new Search.Builder("query1").addIndex("twitter").addType("tweet")
                .addSort(sortByPopulationAsc).build();
        Search search1Duplicate = new Search.Builder("query1").addIndex("twitter").addType("tweet")
                .addSort(sortByPopulationDesc).build();

        assertNotEquals(search1, search1Duplicate);
    }
}
