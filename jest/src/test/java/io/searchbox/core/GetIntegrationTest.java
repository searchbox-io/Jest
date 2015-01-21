package io.searchbox.core;

import io.searchbox.annotations.JestId;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
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
        IndexResponse indexResponse = client().index(new IndexRequest(
                INDEX,
                TYPE,
                "asd/qwe")
                .source("{\"user\":\"tweety\"}"))
                .actionGet();
        assertNotNull(indexResponse);

        JestResult result = client.execute(new Get.Builder(INDEX, "asd/qwe")
                        .type(TYPE)
                        .build()
        );
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void getAsClass() throws IOException {
        String id = "900";
        String message = "checkout my lunch guys!";
        Tweet expectedTweet = new Tweet();
        expectedTweet.setUserHash(id);
        expectedTweet.setMessage(message);

        JestResult result = client.execute(new Index.Builder(expectedTweet).index(INDEX).type(TYPE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        Get get = new Get.Builder(INDEX, id).type(TYPE).build();
        result = client.execute(get);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        Tweet actualTweet = result.getSourceAsObject(Tweet.class);
        assertEquals(expectedTweet.getMessage(), actualTweet.getMessage());
        assertEquals(expectedTweet.getUserHash(), actualTweet.getUserHash());
    }

    @Test
    public void get() throws IOException {
        Get get = new Get.Builder(INDEX, "1").type(TYPE).build();
        JestResult result = client.execute(get);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void getAsynchronously() throws InterruptedException, ExecutionException, IOException {
        client.executeAsync(new Get.Builder(INDEX, "1").type(TYPE).build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                assertTrue(result.getErrorMessage(), result.isSucceeded());
            }

            @Override
            public void failed(Exception ex) {
                fail("failed execution of asynchronous get call");
            }
        });

        //wait for asynchronous call
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getWithType() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setId("testid1");
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").refresh(true).build();
        JestResult indexResult = client.execute(index);
        assertTrue(indexResult.getErrorMessage(), indexResult.isSucceeded());

        JestResult result = client.execute(new Get.Builder("articles", "testid1").type("article").build());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(result.getJsonMap().get("_id"), articleResult.getId());
    }

    @Test
    public void getWithoutType() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setId("testid1");
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").refresh(true).build();
        JestResult indexResult = client.execute(index);
        assertTrue(indexResult.getErrorMessage(), indexResult.isSucceeded());

        JestResult result = client.execute(new Get.Builder("articles", "testid1").build());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(result.getJsonMap().get("_id"), articleResult.getId());
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
