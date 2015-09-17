package io.searchbox.core;

import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class SuggestIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "socializr";
    private static final String TYPE = "meetings";

    @Test
    public void testWithSingleTermSuggester() throws IOException {
        String suggestionName = "my-suggestion";

        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"body\":\"istanbul\"}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"body\":\"amsterdam\"}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"body\":\"rotterdam\"}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"body\":\"vienna\"}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"body\":\"london\"}").refresh(true)).actionGet().isCreated());

        Suggest suggest = new Suggest.Builder("{\n" +
                "  \"" + suggestionName + "\" : {\n" +
                "    \"text\" : \"the amsterdma meetpu\",\n" +
                "    \"term\" : {\n" +
                "      \"field\" : \"body\"\n" +
                "    }\n" +
                "  }\n" +
                "}").build();

        SuggestResult result = client.execute(suggest);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SuggestResult.Suggestion> suggestions = result.getSuggestions(suggestionName);
        assertEquals(3, suggestions.size());

        SuggestResult.Suggestion suggestion1 = suggestions.get(0);
        assertEquals("the", suggestion1.text);
        assertEquals(Integer.valueOf(0), suggestion1.offset);
        assertEquals(Integer.valueOf(3), suggestion1.length);
        assertEquals(0, suggestion1.options.size());

        SuggestResult.Suggestion suggestion2 = suggestions.get(1);
        assertEquals("amsterdma", suggestion2.text);
        assertEquals(Integer.valueOf(4), suggestion2.offset);
        assertEquals(Integer.valueOf(9), suggestion2.length);
        assertEquals(1, suggestion2.options.size());

        SuggestResult.Suggestion suggestion3 = suggestions.get(2);
        assertEquals("meetpu", suggestion3.text);
        assertEquals(Integer.valueOf(14), suggestion3.offset);
        assertEquals(Integer.valueOf(6), suggestion3.length);
        assertEquals(0, suggestion3.options.size());
    }

}
