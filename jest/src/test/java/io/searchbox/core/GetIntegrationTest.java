package io.searchbox.core;

import io.searchbox.annotations.JestId;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class GetIntegrationTest extends AbstractIntegrationTest {

    final static String INDEX = "twitter";
    final static String TYPE = "tweet";

    @Before
    public void setup() throws Exception {
        IndexResponse indexResponse = client().index(new IndexRequest(
                INDEX,
                TYPE,
                "1")
                .source("{\"user\":\"tweety\"}"))
                .actionGet();
        assertTrue(indexResponse.isCreated());
    }

    @Test
    public void getWithSpecialCharacterInDocId() throws IOException {
        final String documentId = "asd/qwe";
        IndexResponse indexResponse = client().index(new IndexRequest(
                INDEX,
                TYPE,
                documentId)
                .source("{\"user\":\"tweety\"}"))
                .actionGet();
        assertNotNull(indexResponse);

        DocumentResult result = client.execute(new Get.Builder(INDEX, documentId)
                        .type(TYPE)
                        .build()
        );
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(documentId, result.getId());
        assertEquals("{\"user\":\"tweety\"}", result.getSourceAsString());
    }

    @Test
    public void getAsClass() throws IOException {
        String id = "900";
        String message = "checkout my lunch guys!";
        Tweet expectedTweet = new Tweet();
        expectedTweet.setUserHash(id);
        expectedTweet.setMessage(message);

        DocumentResult result = client.execute(new Index.Builder(expectedTweet).index(INDEX).type(TYPE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        Get get = new Get.Builder(INDEX, id).type(TYPE).build();
        result = client.execute(get);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        Tweet actualTweet = result.getSourceAsObject(Tweet.class);
        assertEquals(expectedTweet.getMessage(), actualTweet.getMessage());
        assertEquals(expectedTweet.getUserHash(), actualTweet.getUserHash());
        assertEquals("{\"userHash\":\"900\",\"message\":\"checkout my lunch guys!\"}", result.getSourceAsString());
    }

    @Test
    public void get() throws IOException {
        Get get = new Get.Builder(INDEX, "1").type(TYPE).build();
        DocumentResult result = client.execute(get);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void getAsynchronously() throws InterruptedException, ExecutionException, IOException {
        final CountDownLatch completionChecker = new CountDownLatch(1);
        client.executeAsync(new Get.Builder(INDEX, "1").type(TYPE).build(), new JestResultHandler<DocumentResult>() {
            @Override
            public void completed(DocumentResult result) {
                assertTrue(result.getErrorMessage(), result.isSucceeded());
                assertEquals(INDEX, result.getIndex());
                assertEquals(TYPE, result.getType());
                assertEquals("1", result.getId());
                completionChecker.countDown();
            }

            @Override
            public void failed(Exception ex) {
                fail("failed execution of asynchronous get call");
            }
        });

        boolean finishedAsync = completionChecker.await(2, TimeUnit.SECONDS);
        if (!finishedAsync) {
            fail("Execution took to long to complete");
        }
    }

    @Test
    public void getWithType() throws Exception {
        final String id = "testid1";
        final String type = "article";
        TestArticleModel article = new TestArticleModel();
        article.setId(id);
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type(type).refresh(true).build();
        DocumentResult indexResult = client.execute(index);
        assertTrue(indexResult.getErrorMessage(), indexResult.isSucceeded());

        DocumentResult result = client.execute(new Get.Builder("articles", "testid1").type(type).build());
        assertEquals(type, result.getType());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(id, articleResult.getId());
    }

    @Test
    public void getWithoutType() throws Exception {
        final String id = "testid1";
        final String type = "article";
        TestArticleModel article = new TestArticleModel();
        article.setId(id);
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type(type).refresh(true).build();
        DocumentResult indexResult = client.execute(index);
        assertTrue(indexResult.getErrorMessage(), indexResult.isSucceeded());

        DocumentResult result = client.execute(new Get.Builder("articles", "testid1").build());
        assertEquals(type, result.getType());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(id, articleResult.getId());
    }

    class Tweet {
        @JestId
        String userHash;
        String message;

        public String getUserHash() {
            return userHash;
        }

        public void setUserHash(String userHash) {
            this.userHash = userHash;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
