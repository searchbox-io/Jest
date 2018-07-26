package io.searchbox.core;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.core.SearchResult.Hit;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;

/**
 * @author Bobby Hubbard
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class SearchTemplateIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX = "twitter";
    private static final String TYPE = "tweet";

    private static final String QUERY = 
    		"{" +
            "    \"id\": \"templateId\"," +
            "    \"params\": {" +
            "        \"user\" : \"kimchy1\"" +
            "    }" +
            "}";
    
    private static final String INLINE = 
    		"{" +
    		"    \"inline\": {" +
            "    	\"query\": {" +
            "    		\"term\": {" +
            "       		\"user\" : \"{{user}}\"" +
            "    		}" +
            "    	}," +
            "    	\"sort\": \"num\"" +
            "	}," +	
            "   \"params\": {" +
            "        \"user\" : \"kimchy1\"" +
            "    }" +
            "}";


    private static final String SCRIPT =
            "{" +
                    "    \"script\": {" +
                    "       \"lang\": \"mustache\"," +
                    "       \"source\": {" +
                    "           \"query\": {" +
                    "               \"match\": {" +
                    "                   \"user\": \"{{user}}\"" +
                    "               }" +
                    "           }," +
                    "    	    \"sort\": \"num\"" +
                    "       }" +
                    "   }" +
            "}";


    @Test
    public void searchWithValidQuery() throws IOException {
        JestResult result = client.execute(new Search.TemplateBuilder(INLINE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }
    
    @Test
    public void searchTemplateInlineScript() throws Exception {

        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\",\"num\":1}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"abcdef\",\"num\":2}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"abcdef\",\"num\":3}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));

        //template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(INLINE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(1, hits.size());
        Hit<Object, Void> hit0 = hits.get(0);
        
        //check user
        assertEquals("kimchy1", ((Map)hit0.source).get("user"));
    }

    @Test
    public void searchTemplateInlineScriptWithSort() throws Exception {

        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\",\"num\":1}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\",\"num\":0}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"\"}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));

        //template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(INLINE).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2, hits.size());
        Hit<Object, Void> hit0 = hits.get(0);
        Hit<Object, Void> hit1 = hits.get(1);
        
        //check sort
        assertEquals(1, hit0.sort.size());
        assertEquals("0", hit0.sort.get(0));
        assertEquals(1, hit1.sort.size());
        assertEquals("1", hit1.sort.get(0));
        
        //check user
        assertEquals("kimchy1", ((Map)hit0.source).get("user"));
        assertEquals("kimchy1", ((Map)hit1.source).get("user"));
    }
    
    @Test
    public void searchTemplateIdScriptWithSort() throws Exception {
        assertAcked(client().admin().cluster().preparePutStoredScript()
                .setId("templateId")
                .setContent(new BytesArray(SCRIPT), XContentType.JSON)
                .get());
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\",\"num\":1}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"kimchy1\",\"num\":0}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));
        assertTrue(client().index(new IndexRequest(INDEX, TYPE).source("{\"user\":\"\"}", XContentType.JSON).setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)).actionGet().getResult().equals(DocWriteResponse.Result.CREATED));

        //template includes sort
        SearchResult result = client.execute(new Search.TemplateBuilder(QUERY).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        List<SearchResult.Hit<Object, Void>> hits = result.getHits(Object.class);
        assertEquals(2, hits.size());
        Hit<Object, Void> hit0 = hits.get(0);
        Hit<Object, Void> hit1 = hits.get(1);
        
        //check sort
        assertEquals(1, hit0.sort.size());
        assertEquals("0", hit0.sort.get(0));
        assertEquals(1, hit1.sort.size());
        assertEquals("1", hit1.sort.get(0));
        
        //check user
        assertEquals("kimchy1", ((Map)hit0.source).get("user"));
        assertEquals("kimchy1", ((Map)hit1.source).get("user"));
    }	
    
    //TODO: Test file-based templates $ES_CONFIG/scripts/test.mustache  
}
