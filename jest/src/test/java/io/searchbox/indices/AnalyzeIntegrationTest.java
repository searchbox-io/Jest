package io.searchbox.indices;

import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class AnalyzeIntegrationTest extends AbstractIntegrationTest {

    private static String sample_book;

    @BeforeClass
    public static void setupOnce() throws IOException, URISyntaxException {
        sample_book = Resources.toString(Resources.getResource("io/searchbox/sample_book.json"), StandardCharsets.UTF_8);
        assertNotNull(sample_book);
    }

    @Before
    public void setup() {
        createIndex("articles");
    }

    @Test
    public void testWithAnalyzer() throws IOException {
        Action action = new Analyze.Builder()
                .analyzer("standard")
                .text(sample_book)
                .build();
        expectTokens(action, 22);
    }

    @Test
    public void testWithAnalyzerWithTextFormat() throws IOException {
        Action action = new Analyze.Builder()
                .analyzer("standard")
                .text(sample_book)
                //.format("text")
                .build();
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultObj = result.getJsonObject();
        assertNotNull(resultObj);
        JsonArray tokens = resultObj.getAsJsonArray("tokens");
        assertNotNull(tokens);
    }

    @Test
    public void testWithCustomTransientAnalyzer() throws IOException {
        Action action = new Analyze.Builder()
                .tokenizer("keyword")
                .filter("lowercase")
                .text(sample_book)
                .build();
        expectTokens(action, 1);
    }

    private void expectTokens(Action action, int numberOfExpectedTokens) throws IOException {
        JestResult result = client.execute(action);
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        JsonObject resultObj = result.getJsonObject();
        assertNotNull(resultObj);
        JsonArray tokens = resultObj.getAsJsonArray("tokens");
        assertEquals(numberOfExpectedTokens, tokens.getAsJsonArray().size());
    }

}
