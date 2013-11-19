package io.searchbox;

import io.searchbox.annotations.JestId;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.Flush;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */
public class AbstractActionTest {

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        String actual = new Delete.Builder("1").index("twitter").type("tweet").build().buildURI();
        assertEquals(expected, actual);
    }

    @Test
    public void buildUrlWithRequestParameterWithMultipleValues() {
        Action dummyAction = new DummyAction.Builder()
                .setParameter("x", "y")
                .setParameter("x", "z")
                .setParameter("x", "q")
                .setParameter("w", "p")
                .build();
        assertEquals("?w=p&x=z&x=y&x=q&x=z&x=y&x=q&x=z&x=y&x=q", dummyAction.getURI());
    }

    @Test
    public void testEqualsAndHashcode() {
        Action dummyAction1 = new DummyAction.Builder()
                .setParameter("x", "y")
                .setParameter("x", "z")
                .setHeader("X-Custom-Header", "hatsune")
                .build();

        Action dummyAction2 = new DummyAction.Builder()
                .setParameter("x", "y")
                .setParameter("x", "z")
                .setHeader("X-Custom-Header", "hatsune")
                .build();

        Action dummyAction3 = new DummyAction.Builder()
                .setParameter("x", "1")
                .setParameter("x", "z")
                .setHeader("X-Custom_Header", "hatsune")
                .build();

        Action flush = new Flush.Builder().build();

        assertTrue(dummyAction1.equals(dummyAction2));
        assertTrue(dummyAction2.equals(dummyAction1));
        assertEquals(dummyAction1, dummyAction2);
        assertEquals(dummyAction1.hashCode(), dummyAction2.hashCode());

        assertFalse(dummyAction3.equals(dummyAction1));
        assertFalse(dummyAction3.equals(dummyAction2));
        assertFalse(dummyAction1.equals(dummyAction3));
        assertFalse(dummyAction2.equals(dummyAction3));
        assertNotEquals(dummyAction1.hashCode(), dummyAction3.hashCode());
        assertNotEquals(dummyAction2.hashCode(), dummyAction3.hashCode());

        assertFalse(dummyAction1.equals(flush));
        assertFalse(dummyAction2.equals(flush));
        assertFalse(dummyAction3.equals(flush));
        assertNotEquals(dummyAction1.hashCode(), flush.hashCode());
        assertNotEquals(dummyAction2.hashCode(), flush.hashCode());
        assertNotEquals(dummyAction3.hashCode(), flush.hashCode());
    }

    @Test
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get.Builder("twitter", "1").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());

        Delete del = new Delete.Builder("1").index("twitter").type("tweet").build();
        assertEquals("DELETE", del.getRestMethodName());
        assertEquals("GET", get.getRestMethodName());
    }

    @Test
    public void requestDataMultipleClientRequest() {
        Index indexDocument = new Index.Builder("\"indexDocumentData\"").index("index").type("type").id("id").build();
        Update update = new Update.Builder("\"updateData\"").index("indexName").type("indexType").id("1").build();

        assertEquals("\"updateData\"", update.getData(null).toString());
        assertEquals("POST", update.getRestMethodName());
        assertEquals("indexName/indexType/1/_update", update.getURI());

        assertEquals("\"indexDocumentData\"", indexDocument.getData(null).toString());
        assertEquals("PUT", indexDocument.getRestMethodName());
        assertEquals("index/type/id", indexDocument.getURI());
    }

    @Test
    public void getIdFromNullSource() {
        String expected = null;
        String actual = AbstractAction.getIdFromSource(null);
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithoutAnnotation() {
        String expected = null;
        String actual = AbstractAction.getIdFromSource("JEST");
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithAnnotation() {
        String expected = "jest@searchbox.io";
        String actual = AbstractAction.getIdFromSource(new Source("data", "jest@searchbox.io"));
        assertEquals(expected, actual);
    }

    @Test
    public void getIdFromSourceWithAnnotationWithNullId() {
        String expected = null;
        String actual = AbstractAction.getIdFromSource(new Source("data", null));
        assertEquals(expected, actual);
    }

    static class DummyAction extends AbstractAction {
        public DummyAction(Builder builder) {
            super(builder);
            setURI(buildURI());
        }

        @Override
        public String getRestMethodName() {
            return "GET";
        }

        public static class Builder extends AbstractAction.Builder<DummyAction, Builder> {

            @Override
            public DummyAction build() {
                return new DummyAction(this);
            }
        }
    }

    class Source {

        @JestId
        String email;
        String data;

        Source(String data, String email) {
            this.data = data;
            this.email = email;
        }
    }

}