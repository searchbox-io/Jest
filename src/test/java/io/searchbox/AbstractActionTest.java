package io.searchbox;

import io.searchbox.annotations.JestId;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */
public class AbstractActionTest {

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "twitter/tweet/1";
        String actual = new Delete.Builder().id("1").index("twitter").type("tweet").build().buildURI();
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
    public void restMethodNameMultipleClientRequest() {
        Get get = new Get.Builder("1").index("twitter").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());

        Delete del = new Delete.Builder().id("2").build();
        assertEquals("DELETE", del.getRestMethodName());
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
        Index index = new Index.Builder("test").build();
        String expected = null;
        String actual = index.getIdFromSource(new Source("data", null));
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