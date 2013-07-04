package io.searchbox.core;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchIndex;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class GetIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void before() throws Exception {
        Index index = new Index.Builder("{\"user\":\"tweety\"}").index("twitter").type("tweet").id("1").refresh(true).build();
        client.execute(index);
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void getIndex() {
        try {
            executeTestCase(new Get.Builder("1").index("twitter").type("tweet").build());
        } catch (Exception e) {
            fail("Failed during the getting index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    @ElasticsearchIndex(indexName = "twitter")
    public void getIndexAsynchronously() {
        try {
            client.executeAsync(new Get.Builder("1").index("twitter").type("tweet").build(), new JestResultHandler<JestResult>() {
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
    @ElasticsearchIndex(indexName = "articles")
    public void getIndexWithType() throws Exception {
        TestArticleModel article = new TestArticleModel();
        article.setId("testid1");
        article.setName("Jest");
        Index index = new Index.Builder(article).index("articles").type("article").refresh(true).build();
        client.execute(index);

        JestResult result = client.execute(new Get.Builder("testid1").index("articles").type("article").build());
        TestArticleModel articleResult = result.getSourceAsObject(TestArticleModel.class);

        assertEquals(result.getJsonMap().get("_id"), articleResult.getId());
    }

    private void executeTestCase(Get get) throws RuntimeException, IOException {
        JestResult result = client.execute(get);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
