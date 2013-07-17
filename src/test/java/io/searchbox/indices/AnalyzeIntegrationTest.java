package io.searchbox.indices;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.searchbox.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.Assert.*;

/**
 * @author cihat keser
 */
@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class AnalyzeIntegrationTest extends AbstractIntegrationTest {

    private static String sample_book;

    @BeforeClass
    public static void setup() throws IOException, URISyntaxException {
        sample_book = FileUtils.readFileToString(new File(
                Thread.currentThread().getContextClassLoader().getResource("io/searchbox/sample_book.json").toURI()
        ));
        assertNotNull(sample_book);
    }

    @Test
    @ElasticsearchIndex(indexName = "articles")
    public void testWithAnalyzer() throws IOException {
        Action action = new Analyze.Builder()
                .analyzer("standard")
                .source(sample_book)
                .build();
        expectTokens(action, 22);
    }

    @Test
    @ElasticsearchIndex(indexName = "articles")
    public void testWithAnalyzerWithTextFormat() throws IOException {
        Action action = new Analyze.Builder()
                .analyzer("standard")
                .source(sample_book)
                .format("text")
                .build();
        JestResult result = client.execute(action);
        assertNotNull(result);
        JsonObject resultObj = result.getJsonObject();
        assertNotNull(resultObj);
        JsonPrimitive tokens = resultObj.getAsJsonPrimitive("tokens");
        assertNotNull(tokens);
        assertTrue(tokens.isString());
    }

    @Test
    @ElasticsearchIndex(indexName = "articles")
    public void testWithCustomTransientAnalyzer() throws IOException {
        Action action = new Analyze.Builder()
                .tokenizer("keyword")
                .filter("lowercase")
                .source(sample_book)
                .build();
        expectTokens(action, 1);
    }

    private void expectTokens(Action action, int numberOfExpectedTokens) throws IOException {
        JestResult result = client.execute(action);
        assertNotNull(result);
        JsonObject resultObj = result.getJsonObject();
        assertNotNull(resultObj);
        JsonArray tokens = resultObj.getAsJsonArray("tokens");
        assertEquals(numberOfExpectedTokens, tokens.getAsJsonArray().size());
    }

}
