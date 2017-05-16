package io.searchbox.core;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import org.junit.Test;

import static org.junit.Assert.*;


public class SearchScrollTest {
    @Test
    public void methodIsGetIfScrollIdIsShort() {
        String scrollId = Strings.padStart("scrollId", SearchScroll.MAX_SCROLL_ID_LENGTH, 'x');
        Action searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI();

        assertEquals("GET", searchScroll.getRestMethodName());
        assertNull(searchScroll.getData(new Gson()));
        assertTrue(uri.length() < 2000);
        assertTrue(uri.contains(scrollId));
    }

    @Test
    public void methodIsPostIfScrollIdIsLong() {
        String scrollId = Strings.padStart("scrollId", 2000, 'x');

        JsonObject expectedResults = new JsonObject();
        expectedResults.addProperty("scroll_id", scrollId);

        Action searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI();

        assertEquals("POST", searchScroll.getRestMethodName());
        assertEquals(expectedResults.toString(), searchScroll.getData(new Gson()));
        assertTrue(uri.length() < 2000);
        assertFalse(uri.contains(scrollId));
    }

    @Test
    public void equalsReturnsTrueForSameScrollIds() {
        SearchScroll searchScroll1 = new SearchScroll.Builder("scroller1", "scroll").build();
        SearchScroll searchScroll1Duplicate = new SearchScroll.Builder("scroller1", "scroll").build();

        assertEquals(searchScroll1, searchScroll1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentScrollIds() {
        SearchScroll searchScroll1 = new SearchScroll.Builder("scroller1", "scroll").build();
        SearchScroll searchScroll2 = new SearchScroll.Builder("scroller2", "scroll").build();

        assertNotEquals(searchScroll1, searchScroll2);
    }

    @Test
    public void equalsReturnsFalseForDifferentScrollNames() {
        SearchScroll searchScroll1 = new SearchScroll.Builder("scroller", "scroll").build();
        SearchScroll searchScroll2 = new SearchScroll.Builder("scroller", "scroll2").build();

        assertNotEquals(searchScroll1, searchScroll2);
    }

}
