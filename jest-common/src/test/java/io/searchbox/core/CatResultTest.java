package io.searchbox.core;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Bartosz Polnik
 */
public class CatResultTest {
    private final String EXAMPLE_RESPONSE_TWO_ROWS = "[\n" +
            "    {\n" +
            "        \"alias\": \"testAlias\",\n" +
            "        \"index\": \"testIndex\",\n" +
            "        \"filter\": \"-\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"alias\": \"testAlias2\",\n" +
            "        \"filter\": \"-\",\n" +
            "        \"index\": \"testIndex2\"\n" +
            "    }\n" +
            "]";

    private final String EXAMPLE_RESPONSE_SINGLE_ROWS = "[\n" +
            "    {\n" +
            "        \"alias\": \"testAlias\",\n" +
            "        \"index\": \"testIndex\",\n" +
            "        \"filter\": \"-\"\n" +
            "    }" +
            "]";

    @Test
    public void shouldReturnEmptyArrayOnNoJsonObject() {
        CatResult catResult = new CatResult(new Gson());
        assertArrayEquals(new String[0][0], catResult.getPlainText());
    }

    @Test
      public void shouldReturnArrayWithColumnNamesAndSingleResult() {
        Cat cat = new Cat.IndicesBuilder().build();
        String reasonPhase = "";
        CatResult catResult = cat.createNewElasticSearchResult(EXAMPLE_RESPONSE_SINGLE_ROWS, 200, reasonPhase, new Gson());

        assertArrayEquals(new String[][]{
                new String[]{"alias", "index", "filter"},
                new String[]{"testAlias", "testIndex", "-"}
        }, catResult.getPlainText());
    }

    @Test
    public void shouldReturnArrayWithTwoResultsEventWhenColumnsWereReordered() {
        Cat cat = new Cat.IndicesBuilder().build();
        String reasonPhase = "";
        CatResult catResult = cat.createNewElasticSearchResult(EXAMPLE_RESPONSE_TWO_ROWS, 200, reasonPhase, new Gson());

        assertArrayEquals(new String[][]{
                new String[]{"alias", "index", "filter"},
                new String[]{"testAlias", "testIndex", "-"},
                new String[]{"testAlias2", "testIndex2", "-"}
        }, catResult.getPlainText());
    }
}
