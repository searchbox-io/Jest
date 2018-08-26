package io.searchbox.indices.type;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.elasticsearch.action.DocWriteResponse.Result.CREATED;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class TypeExistIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX_NAME = "it_typexst_0";
    static final String EXISTING_INDEX_TYPE = "ittypex";
    static final String NON_EXISTING_INDEX_TYPE = "ittypnonex";

    @Before
    public void setup() {
        prepareCreate(INDEX_NAME)
                .addMapping(EXISTING_INDEX_TYPE)
                .execute();
        ensureSearchable(INDEX_NAME);
    }

    @Test
    public void indexTypeExists() throws IOException, InterruptedException {
        IndexResponse indexResponse = client().index(
                new IndexRequest(INDEX_NAME, EXISTING_INDEX_TYPE)
                        .source("{\"user\":\"tweety\"}", XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
        ).actionGet();
        assertTrue(indexResponse.getResult().equals(CREATED));

        Action typeExist = new TypeExist.Builder(INDEX_NAME).addType(EXISTING_INDEX_TYPE).build();
        JestResult result = client.execute(typeExist);

        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void indexTypeNotExists() throws IOException {
        Action typeExist = new TypeExist.Builder(INDEX_NAME).addType(NON_EXISTING_INDEX_TYPE).build();

        JestResult result = client.execute(typeExist);
        assertFalse(result.isSucceeded());
    }

}
