package io.searchbox.cluster;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * @author Neil Gentleman
 */

@RunWith(ElasticsearchRunner.class)
@ElasticsearchNode
public class HealthIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void health() throws Exception {
        JestResult result = client.execute(new Health.Builder().build());
        assertNotNull(result);
        assertThat(result.getJsonObject().get("status").getAsString(), anyOf(equalTo("green"), equalTo("yellow"), equalTo("red")));
        assertTrue(result.isSucceeded());
    }

}
