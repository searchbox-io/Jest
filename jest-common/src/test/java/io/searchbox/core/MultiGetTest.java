package io.searchbox.core;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */
public class MultiGetTest {

    @Test
    public void getMultipleDocs() {
        Doc doc1 = new Doc("twitter", "tweet", "1");
        Doc doc2 = new Doc("twitter", "tweet", "2");
        Doc doc3 = new Doc("twitter", "tweet", "3");
        List<Doc> docs = Arrays.asList(doc1, doc2, doc3);

        MultiGet get = new MultiGet.Builder.ByDoc(docs).build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("/_mget", get.getURI());
        assertEquals("{\"docs\":[" +
                "{\"_type\":\"tweet\",\"_id\":\"1\",\"_index\":\"twitter\"}," +
                "{\"_type\":\"tweet\",\"_id\":\"2\",\"_index\":\"twitter\"}," +
                "{\"_type\":\"tweet\",\"_id\":\"3\",\"_index\":\"twitter\"}]}", get.getData(new Gson()));
    }

    @Test
    public void getDocumentWithMultipleIds() {
        MultiGet get = new MultiGet.Builder.ById("twitter", "tweet").addId(Arrays.asList("1", "2", "3")).build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/_mget", get.getURI());
        assertEquals("{\"ids\":[\"1\",\"2\",\"3\"]}", get.getData(new Gson()));
    }

}
