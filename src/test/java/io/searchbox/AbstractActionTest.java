package io.searchbox;

import io.searchbox.annotations.JESTID;
import io.searchbox.core.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class AbstractActionTest {

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        String actual = new Delete.Builder("1").build().buildURI(new Doc("twitter", "tweet", "1"));
        assertEquals(expected, actual);
    }

    @Test
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get.Builder("1").index("twitter").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());

        Delete del = new Delete.Builder("2").build();
        assertEquals("DELETE", del.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());

        Percolate percolate = new Percolate("Celtic", "Boston", "{\"Really good query\"}");
        assertEquals("PUT", percolate.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());

    }

    @Test
    public void requestDataMultipleClientRequest() {
        Index indexDocument = new Index.Builder("\"indexDocumentData\"").index("index").type("type").id("id").build();
        Update update = new Update.Builder("\"updateData\"").index("indexName").type("indexType").id("1").build();

        assertEquals("\"updateData\"", update.getData().toString());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("indexName/indexType/1/_update", update.getURI());

        assertEquals("\"indexDocumentData\"", indexDocument.getData().toString());
        assertEquals("PUT", indexDocument.getRestMethodName());
        assertEquals("index/type/id", indexDocument.getURI());
    }


    @Test
    public void getIdFromNullSource() {
        Index index = new Index.Builder("test").build();
        String expected = null;
        String actual = index.getIdFromSource(null);
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithoutAnnotation() {
        Index index = new Index.Builder("test").build();
        String expected = null;
        String actual = index.getIdFromSource("JEST");
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithAnnotation() {
        Index index = new Index.Builder("test").build();
        String expected = "jest@searchbox.io";
        String actual = index.getIdFromSource(new Source("data", "jest@searchbox.io"));
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithAnnotationWithNullId() {
        Index index =new Index.Builder("test").build();
        String expected = null;
        String actual = index.getIdFromSource(new Source("data", null));
        assertEquals(expected, actual);
    }


    class Source {

        @JESTID
        String email;

        String data;

        Source(String data, String email) {
            this.data = data;
            this.email = email;
        }
    }

}