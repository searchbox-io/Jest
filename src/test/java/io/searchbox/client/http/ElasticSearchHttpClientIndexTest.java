package io.searchbox.client.http;

import io.searchbox.client.ElasticSearchClient;
import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.indices.Index;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClientIndexTest {

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
    }

    @After
    public void tearDown() throws Exception {
        context.close();
    }

    @Test
    public void createIndexWithValidParameters() {
        try {
            client.index(new Index("twitter", "tweet", "1", "{user:\"searchboxio\"}"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void createIndexWithNullIndexName() throws Exception {
        client.index(new Index(null, "tweet", "1", "{user:'searchboxio'}"));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexWithNullIndexType() throws Exception {
        client.index(new Index("twitter", null, "1", "{user:'searchboxio'}"));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexWithNullIndexId() throws Exception {
        client.index(new Index("twitter", "tweet", null, "{user:'searchboxio'}"));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexWithNullData() {
        client.indexAsync(new Index("twitter", "tweet", "1", null));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexWithNullIndex() {
        client.indexAsync(null);
    }

    @Test
    public void createIndexAsyncWithValidParameters() {
        try {
            client.indexAsync(new Index("twitter", "tweet", "1", "{user:'searchboxio'}"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test(expected = RuntimeException.class)
    public void createIndexAsyncWithNullIndexName() {
        client.indexAsync(new Index(null, "tweet", "1", "{user:'searchboxio'}"));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexAsyncWithNullIndexType() {
        client.indexAsync(new Index("twitter", null, "1", "{user:'searchboxio'}"));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexAsyncWithNullIndexId() {
        client.indexAsync(new Index("twitter", "tweet", null, "{user:'searchboxio'}"));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexAsyncWithNullData() {
        client.indexAsync(new Index("twitter", "tweet", "1", null));
    }

    @Test(expected = RuntimeException.class)
    public void createIndexAsyncWithNullIndex() {
        client.indexAsync(null);
    }


}
