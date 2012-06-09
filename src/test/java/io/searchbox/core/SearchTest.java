package io.searchbox.core;

import io.searchbox.Document;
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


public class SearchTest {


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
    public void searchWithValidQuery() {
      Object query = "{\n" +
              "    \"query\": {\n" +
              "        \"filtered\" : {\n" +
              "            \"query\" : {\n" +
              "                \"query_string\" : {\n" +
              "                    \"query\" : \"some query string here\"\n" +
              "                }\n" +
              "            },\n" +
              "            \"filter\" : {\n" +
              "                \"term\" : { \"user\" : \"kimchy\" }\n" +
              "            }\n" +
              "        }\n" +
              "    }\n" +
              "}";
        try {
            client.execute(new Search("twitter","tweet",query));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }
}
