package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.index.IndexService;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author ferhat
 * @author cihat keser
 */
@ESIntegTestCase.ClusterScope(scope = ESIntegTestCase.Scope.TEST, numDataNodes = 1)
public class PutMappingIntegrationTest extends AbstractIntegrationTest {

    private static final String INDEX_NAME = "mapping_index";
    private static final String INDEX_TYPE = "document";

    @Before
    public void setup() {
        createIndex(INDEX_NAME);
        ensureSearchable(INDEX_NAME);
    }

    @Test
    public void testPutMapping() throws IOException {
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                "{ \"document\" : { \"properties\" : { \"message_1\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
        ).build();

        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testPutMappingWithDocumentMapperBuilder() throws IOException {
        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(INDEX_TYPE)
                .add(new StringFieldMapper.Builder("message_2").store(true));

        GetSettingsResponse getSettingsResponse =
                client().admin().indices().getSettings(new GetSettingsRequest().indices(INDEX_NAME)).actionGet();
        MapperService mapperService = getMapperService();
        DocumentMapper documentMapper = new DocumentMapper.Builder(
                getSettingsResponse.getIndexToSettings().get(INDEX_NAME),
                rootObjectMapperBuilder,
                mapperService)
                .build(mapperService, mapperService.documentMapperParser());
        String expectedMappingSource = documentMapper.mappingSource().toString();
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                expectedMappingSource
        ).build();

        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    private MapperService getMapperService() {
        ClusterState clusterState = internalCluster().clusterService().state();
        ShardRouting shardRouting = clusterState.routingTable().index(INDEX_NAME).shard(0).getShards().get(0);
        String nodeName = clusterState.getNodes().get(shardRouting.currentNodeId()).getName();

        IndicesService indicesService = internalCluster().getInstance(IndicesService.class, nodeName);
        IndexService indexService = indicesService.indexService(INDEX_NAME);
        return indexService.mapperService();
    }
}
