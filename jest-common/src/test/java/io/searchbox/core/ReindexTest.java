package io.searchbox.core;

import com.google.gson.Gson;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by ozlevka on 5/17/16.
 */
public class ReindexTest {
    @Test
    public void testReindexUri() {
        Reindex.Builder b = new Reindex.Builder("old_index", "new_index");

        Reindex ri = b.build();

        String s = ri.getURI();
        assertTrue(s.equals("/_reindex?wait_for_completion=true"));
    }

    @Test
    public void testReindexBuilderData() {
        Reindex.Builder b = new Reindex.Builder("old_index", "new_index");
        try {
            b.operationType("create")
              .query("{\n" +
                      "      \"term\": {\n" +
                      "        \"user\": \"kimchy\"\n" +
                      "      }\n" +
                      "    }")
              .srcDocumentType("tweet")
              .versionType("internal")
              .conflicts("proceed");
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        Reindex r = b.build();

        assertTrue(r.getRestMethodName().equals("POST"));
        Gson g = new Gson();
        String data = r.getData(g);

        assertTrue(data.length() > 0);

        Map<String, Object> test = g.fromJson(data, Map.class);

        assertNotNull(test);
        assertEquals(test.get("conflicts"),"proceed");
        assertEquals(((Map<String, Object>)test.get("source")).get("index"),"old_index");
        assertEquals(((Map<String, Object>)test.get("dest")).get("index"),"new_index");
    }
}