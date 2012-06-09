package io.searchbox.core;


import io.searchbox.client.SpringClientTestConfiguration;
import io.searchbox.client.http.ElasticSearchHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.fail;

/**
 * @author Dogukan Sonmez
 */


public class PercolateTest {
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
    public void percolateWithValidParameters() {
        String query = "{\n" +
                "    \"query\" : {\n" +
                "        \"term\" : {\n" +
                "            \"field1\" : \"value1\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        try {
            client.execute(new Percolate("twitter", "kuku", query));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

}