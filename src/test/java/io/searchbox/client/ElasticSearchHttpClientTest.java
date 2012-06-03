package io.searchbox.client;

import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.indices.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.LinkedHashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchHttpClientTest {

    ElasticSearchHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = new ElasticSearchHttpClient();
    }

    @Test
    public void buildRestUrlWithValidParameters() {
        String expected = "http://localhost:9200/twitter/tweet/1";
        String actual = client.buildRestUrl(new Index("twitter", "tweet", "1", "{user:\"searchboxio\"}"), "http://localhost:9200");
        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithValidIndexInvalidServer() {
        client.buildRestUrl(new Index("twitter", "tweet", "1", "{user:\"searchboxio\"}"), "------");
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithInvalidIndexValidServer() {
        client.buildRestUrl(new Index("@@@@@@", "", "$$$$", "{user}"), "http://localhost:9200");
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithUValidParameters() {
        client.buildRestUrl(new Index("", "^^^^^^^", "", "{user:\"searchboxio\"}"), "9200");
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithNullParameters() {
        client.buildRestUrl(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void buildRestUrlWithEmptyParameters() {
        client.buildRestUrl(new Index(), "");
    }

    @Test
    public void getElasticSearchServer() {
        LinkedHashSet<String> servers = new LinkedHashSet<String>();
        String server = "http://localhost:9200";
        servers.add(server);
        client.setServers(servers);
        assertEquals(server, client.getElasticSearchServer());
    }

    @Test(expected = RuntimeException.class)
    public void getElasticSearchServerWithNullServer() {
        client.setServers(null);
        client.getElasticSearchServer();
    }

}
