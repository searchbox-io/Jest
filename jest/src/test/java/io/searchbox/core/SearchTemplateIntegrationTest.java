package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

/**
 * @author Bobby Hubbard
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class SearchTemplateIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "twitter";
    private static final String TYPE = "tweet";

    String query = "{" +
            "    \"id\": \"templateId\"," +
            "    \"params\": {" +
            "        \"user\" : \"kimchy1\"" +
            "    }" +
            "}";
    
    String inline = "{" +
    		"    \"inline\": {" +
            "    	\"query\": {" +
            "    		\"term\": {" +
            "       		\"user\" : \"{{user}}\"" +
            "    		}" +
            "    	}," +
            "    	\"sort\": \"user\"" +
            "	}," +	
            "   \"params\": {" +
            "        \"user\" : \"kimchy1\"" +
            "    }" +
            "}";
    
    String template = "{" +
    		"    \"template\": {" +
            "    	\"query\": {" +
            "    		\"term\": {" +
            "       		\"user\" : \"{{user}}\"" +
            "    		}" +
            "    	}," +
            "    	\"sort\": \"user\"" +
            "	}" +	
            "}";
    

    @Test
    public void searchWithValidQuery() throws IOException {
        JestResult result = client.execute(new Search.TemplateBuilder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void searchTemplateInlineScriptWithSort() throws Exception { 
    	    	
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\"}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"\"}").refresh(true)).actionGet().isCreated());

        //template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(inline).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(1, hits.get(0).sort.size());
        assertEquals("kimchy1", hits.get(0).sort.get(0));
        assertEquals(1, hits.get(1).sort.size());
        assertEquals("", hits.get(1).sort.get(0));
    }
    
    @Test
    public void searchTemplateIdScriptWithSort() throws Exception { 
    	assertTrue(client().index(new IndexRequest(".scripts", "mustache", "templateId").source(template).refresh(true)).actionGet().isCreated());    	
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\"}").refresh(true)).actionGet().isCreated());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"\"}").refresh(true)).actionGet().isCreated());

        //template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(query).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(1, hits.get(0).sort.size());
        assertEquals("kimchy1", hits.get(0).sort.get(0));
        assertEquals(1, hits.get(1).sort.size());
        assertEquals("", hits.get(1).sort.get(0));
    }	
}
