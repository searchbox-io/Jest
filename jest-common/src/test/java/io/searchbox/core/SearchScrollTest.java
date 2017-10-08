package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.Action;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;


public class SearchScrollTest {
    @Test
    public void methodIsGetIfScrollIdIsShort() {
        String scrollId = StringUtils.leftPad("scrollId", SearchScroll.MAX_SCROLL_ID_LENGTH, 'x');
        Action searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI();

        assertEquals("GET", searchScroll.getRestMethodName());
        assertNull(searchScroll.getData(new Gson()));
        assertTrue(uri.length() < 2000);
        assertTrue(uri.contains(scrollId));
    }

    @Test
    public void methodIsPostIfScrollIdIsLong() {
        String scrollId = StringUtils.leftPad("scrollId", 2000, 'x');
        Action searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI();

        assertEquals("POST", searchScroll.getRestMethodName());
        assertEquals(scrollId, searchScroll.getData(new Gson()));
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
