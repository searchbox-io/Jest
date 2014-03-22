package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numNodes = 1)
public class GetIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void setup() throws Exception {
        IndexResponse indexResponse = client().index(new IndexRequest(
                "twitter",
                "tweet",
                "1")
                .source("{\"user\":\"tweety\"}"))
                .actionGet();
        assertTrue(indexResponse.isCreated());
    }

    @Test
    public void getIndexWithSpecialCharsinDocId() throws IOException {
        IndexResponse indexResponse = client().index(new IndexRequest(
                "twitter",
                "tweet",
                "asd/qwe")
                .source("{\"user\":\"tweety\"}"))
                .actionGet();
        assertNotNull(indexResponse);

        JestResult result = client.execute(new Get.Builder("twitter", "asd/qwe")
                .type("tweet")
                .build()
        );
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    @Test
    public void getIndex() {
        try {
            executeTestCase(new Get.Builder("twitter", "1").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void getIndexAsynchronously() {
        try {
            client.executeAsync(new Get.Builder("twitter", "1").type("tweet").build(), new JestResultHandler<JestResult>() {
                @Override
                public void completed(JestResult result) {
                    assertNotNull(result);
                    assertTrue(result.isSucceeded());
                }

                @Override
                public void failed(Exception ex) {
                    fail("failed execution of asynchronous get call");
                }
            });
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }

        //wait for asynchronous call
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getIndexWithType() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setId("testid1");
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").refresh(true).build();
        client.execute(index);

        JestResult result = client.execute(new Get.Builder("articles", "testid1").type("article").build());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(result.getJsonMap().get("_id"), articleResult.getId());
    }

    @Test
    public void getIndexWithoutType() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setId("testid1");
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").refresh(true).build();
        client.execute(index);

        JestResult result = client.execute(new Get.Builder("articles", "testid1").build());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(result.getJsonMap().get("_id"), articleResult.getId());
    }

    private void executeTestCase(Get get) throws RuntimeException, IOException {
        JestResult result = client.execute(get);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
