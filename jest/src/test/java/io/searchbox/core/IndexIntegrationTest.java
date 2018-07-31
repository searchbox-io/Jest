package io.searchbox.core;

import com.google.common.collect.ImmutableMap;
import io.searchbox.client.AbstractJestClient;
import io.searchbox.common.AbstractIntegrationTest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.SUITE, numDataNodes = 1)
public class IndexIntegrationTest extends AbstractIntegrationTest {

    static final String INDEX = "twitter";
    static final String TYPE = "tweet";

    @Test
    public void indexDocumentWithValidParametersAndWithoutSettings() throws IOException {
        String id = "1000";
        Map<String, String> source = ImmutableMap.of(
                "test_name", "indexDocumentWithValidParametersAndWithoutSettings");

        DocumentResult result = client.execute(
                new Index.Builder(source)
                        .index(INDEX)
                        .type(TYPE)
                        .id(id)
                        .refresh(true)
                        .build()
        );
        assertTrue(result.getErrorMessage(), result.isSucceeded());
        assertEquals(INDEX, result.getIndex());
        assertEquals(TYPE, result.getType());
        assertEquals(id, result.getId());

        GetResponse getResponse = get(INDEX, TYPE, id);
        assertTrue(getResponse.isExists());
        assertFalse(getResponse.isSourceEmpty());
        assertEquals(source, getResponse.getSource());
    }

    @Test
    public void automaticIdGeneration() throws IOException {
        Map<String, String> source = ImmutableMap.of("test_name", "automaticIdGeneration");

        DocumentResult result = client.execute(
                new Index.Builder(source)
                        .index(INDEX)
                        .type(TYPE)
                        .refresh(true)
                        .build()
        );
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        String id = result.getId();
        GetResponse getResponse = get(INDEX, TYPE, id);
        assertTrue(getResponse.isExists());
        assertFalse(getResponse.isSourceEmpty());
        assertEquals(source, getResponse.getSource());
    }

    @Test
    public void indexDocumentWithDateField() throws Exception {
        SimpleDateFormat defaultDateFormat = new SimpleDateFormat(AbstractJestClient.ELASTIC_SEARCH_DATE_FORMAT);
        Date creationDate = new Date(1356998400000l); // Tue, 01 Jan 2013 00:00:00 GMT
        String id = "1008";
        Map source = ImmutableMap.of("user", "jest", "creationDate", creationDate);
        String mapping = "{ \"properties\" : { \"creationDate\" : {\"type\" : \"date\"} } }";

        createIndex(INDEX);
        assertTrue(client().admin().indices().putMapping(new PutMappingRequest(INDEX)
                .type(TYPE).source(mapping, XContentType.JSON)).actionGet().isAcknowledged());
        assertConcreteMappingsOnAll(INDEX, TYPE, "creationDate");

        DocumentResult result = client.execute(new Index.Builder(source).index(INDEX).type(TYPE).id(id).build());
        assertTrue(result.getErrorMessage(), result.isSucceeded());

        GetResponse getResponse = get(INDEX, TYPE, id);
        assertTrue(getResponse.isExists());
        assertFalse(getResponse.isSourceEmpty());
        Map actualSource = getResponse.getSource();
        assertEquals("jest", actualSource.get("user"));
        assertEquals(creationDate, defaultDateFormat.parse((String) actualSource.get("creationDate")));
    }

}
