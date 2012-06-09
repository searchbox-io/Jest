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


public class GetTest {
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
    public void getIndex() {
        Document document = new Document("twitter", "tweet", "1");
        try {
            client.execute(new Get(document));
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

}
