package io.searchbox.core;

import io.searchbox.action.Action;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SuggestTest {

    @Test
    public void getURIWithoutIndexAndType() {
        Action suggest = new Suggest.Builder("").build();
        assertEquals("_all/_suggest", suggest.getURI());
    }

    @Test
    public void getURIWithOnlyOneIndex() {
        Action suggest = new Suggest.Builder("").addIndex("twitter").build();
        assertEquals("twitter/_suggest", suggest.getURI());
    }

    @Test
    public void getURIWithOneIndexAndOneType() {
        Action suggest = new Suggest.Builder("").addIndex("twitter").addType("tweet").build();
        assertEquals("twitter/tweet/_suggest", suggest.getURI());
    }

    @Test
    public void getURIWithOnlyOneType() {
        Action suggest = new Suggest.Builder("").addType("tweet").build();
        assertEquals("_all/tweet/_suggest", suggest.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleIndex() {
        Action suggest = new Suggest.Builder("").addIndex("twitter").addIndex("suggestbox").build();
        assertEquals("twitter%2Csuggestbox/_suggest", suggest.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        Action suggest = new Suggest.Builder("").addType("tweet").addType("jest").build();
        assertEquals("_all/tweet%2Cjest/_suggest", suggest.getURI());
    }

    @Test
    public void getURIWithMultipleIndexAndTypes() {
        Action suggest = new Suggest.Builder("")
            .addIndex("twitter")
            .addIndex("suggestbox")
            .addType("tweet")
            .addType("jest")
            .build();
        assertEquals("twitter%2Csuggestbox/tweet%2Cjest/_suggest", suggest.getURI());
    }

    @Test
    public void getDataSimple() {
        String query = "{\"elementSuggestion\":{\"text\":\"courgette\",\"completion\":{\"field\":\"element.completion\",\"size\":10,\"fuzzy\":{}}}}";
        Action suggest = new Suggest.Builder(query).build();

        assertNotNull("data", suggest.getData(null));
        assertEquals(query, suggest.getData(null).toString());
    }
}
