package io.searchbox.core;

import com.google.gson.Gson;
import io.searchbox.action.Action;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class SearchScrollTest {
    @Test
    public void preformGetIfScrollIdShortEnough() {
        String scrollId = StringUtils.leftPad("scrollId", SearchScroll.MAX_SCROLL_ID_LENGTH, 'x');
        Action searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI();

        assertEquals("GET", searchScroll.getRestMethodName());
        assertNull(searchScroll.getData(new Gson()));
        assertTrue(uri.length() < 2000);
        assertTrue(uri.contains(scrollId));
    }

    @Test
    public void preformPostIfScrollIdLongEnough() {
        String scrollId = StringUtils.leftPad("scrollId", 2000, 'x');
        Action searchScroll = new SearchScroll.Builder(scrollId, "1m").build();
        String uri = searchScroll.getURI();

        assertEquals("POST", searchScroll.getRestMethodName());
        assertEquals(scrollId, searchScroll.getData(new Gson()));
        assertTrue(uri.length() < 2000);
        assertFalse(uri.contains(scrollId));
    }
}
