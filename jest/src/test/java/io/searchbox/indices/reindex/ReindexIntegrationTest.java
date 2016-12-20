package io.searchbox.indices.reindex;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;

import java.io.IOException;

import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author fabien baligand
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class ReindexIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testReindex() throws IOException {
        String sourceIndex = "my_source_index";
        String destIndex = "my_dest_index";
        String documentType = "my_type";
		String documentId = "my_id";
        
        createIndex(sourceIndex);
		index(sourceIndex, documentType, documentId, "{}");

    	ImmutableMap<String, Object> source = ImmutableMap.<String, Object>of("index", sourceIndex);
    	ImmutableMap<String, Object> dest = ImmutableMap.<String, Object>of("index", destIndex);
    	Reindex reindex = new Reindex.Builder(source, dest).build();
    	JestResult result = client.execute(reindex);
        
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertTrue(indexExists(destIndex));
        assertTrue(get(destIndex, documentType, documentId).isExists());
    }

}
