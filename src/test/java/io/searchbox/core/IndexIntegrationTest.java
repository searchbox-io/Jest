package io.searchbox.core;

import io.searchbox.ElasticSearchTestServer;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.client.http.ElasticSearchHttpClient;
import io.searchbox.configuration.SpringClientTestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class IndexIntegrationTest {

    private AnnotationConfigApplicationContext context;

    ElasticSearchHttpClient client;

    Map source;

    @Before
    public void setUp() throws Exception {
        source = new HashMap<Object,Object>();
        context = new AnnotationConfigApplicationContext(SpringClientTestConfiguration.class);
        client = context.getBean(ElasticSearchHttpClient.class);
        ElasticSearchTestServer.start();
        ElasticSearchTestServer.setResponseEntity("{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}");
    }

    @After
    public void tearDown() throws Exception {
        context.close();
        ElasticSearchTestServer.setResponseEntity("");
    }

    @Test
    public void indexDocumentWithValidParametersAndWithoutSettings() throws IOException {
        try {
            source.put("user","searchbox");
            executeTestCase(new Index("twitter", "tweet", "1",source));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void automaticIdGeneration() {
        try {
            source.put("user","jest");
            executeTestCase(new Index("Twitter", "tweet", source));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addMultipleSourceAtOnce() {
        try {
            executeTestCase(new Index("twitter", "tweet", createTestSource()));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndex() {
        client.registerDefaultIndex("twitter");
        source.put("user","dogukan");
        try {
            executeTestCase(new Index( "tweet", source,"1"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexWithoutId() {
        client.registerDefaultIndex("twitter");
        source.put("user","cool user");
        try {
            executeTestCase(new Index("tweet", source));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentsToDefaultIndex() {
        client.registerDefaultIndex("twitter");
        try {
            executeTestCase(new Index("tweet",createTestSource()));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    @Test
    public void addDocumentToDefaultIndexAndDefaultType() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        source.put("user","admin");
        try {
            executeTestCase(new Index(source,"1"));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void addDocumentToDefaultIndexAndTypeWithoutId() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        source.put("user","sonmez");
        try {
            executeTestCase(new Index(source));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }


    @Test
    public void addDocumentsToDefaultIndexAndTypeWithoutId() {
        client.registerDefaultIndex("twitter");
        client.registerDefaultType("tweet");
        try {
            executeTestCase(new Index(createTestSource()));
        } catch (Exception e) {
            fail("Failed during the create index with valid parameters. Exception:%s" + e.getMessage());
        }
    }

    private List<Object> createTestSource() {
        List<Object> sources = new ArrayList<Object>();
        TestObject object1 = new TestObject("object1","value1");
        TestObject object2 = new TestObject("object2","value2");
        TestObject object3 = new TestObject("object3","value3");
        sources.add(object1);
        sources.add(object2);
        sources.add(object3);
        return sources;
    }

    class TestObject{
        String field1;
        String field2;
        TestObject(String field1, String field2){
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    private void executeTestCase(Index index) throws RuntimeException, IOException {
        ElasticSearchResult result = client.execute(index);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }
}
