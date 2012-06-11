package io.searchbox.core;


import io.searchbox.Document;
import io.searchbox.Source;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class UpdateTest {

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
    public void updateWithValidParameters() {
        Document document = new Document("twitter", "tweet", "1");
        document.setSource(new Source("{\n" +
                "    \"script\" : \"ctx._source.tags += tag\",\n" +
                "    \"params\" : {\n" +
                "        \"tag\" : \"blue\"\n" +
                "    }\n" +
                "}"));
        try {
            ElasticSearchResult result = client.execute(new Update(document));
            assertNotNull(result);
            assertTrue(result.isSucceeded());
        } catch (Exception e) {
            fail("Failed during the delete index with valid parameters. Exception:%s" + e.getMessage());
        }
    }
}
