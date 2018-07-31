package io.searchbox.indices;

import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.KeywordFieldMapper;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.mapper.RootObjectMapper;
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
                "{ \"document\" : { \"properties\" : { \"message_1\" : {\"type\" : \"text\", \"store\" : \"true\"} } } }"
        ).build();

        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    @Test
    public void testPutMappingWithDocumentMapperBuilder() throws Exception {
        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder(INDEX_TYPE)
                .add(new KeywordFieldMapper.Builder("message_2").store(true));
        MapperService mapperService = getMapperService();
        DocumentMapper documentMapper = new DocumentMapper.Builder(rootObjectMapperBuilder, mapperService)
                .build(mapperService);
        String expectedMappingSource = documentMapper.mappingSource().toString();
        PutMapping putMapping = new PutMapping.Builder(
                INDEX_NAME,
                INDEX_TYPE,
                expectedMappingSource
        ).build();

        JestResult result = client.execute(putMapping);
        assertTrue(result.getErrorMessage(), result.isSucceeded());
    }

    private MapperService getMapperService() throws Exception {
        ClusterState clusterState = internalCluster().clusterService().state();
        ShardRouting shardRouting = clusterState.routingTable().index(INDEX_NAME).shard(0).getShards().get(0);
        String nodeName = clusterState.getNodes().get(shardRouting.currentNodeId()).getName();

        ClusterService clusterService = internalCluster().getInstance(ClusterService.class, nodeName);
        IndicesService indexServices = internalCluster().getInstance(IndicesService.class, nodeName);
        return indexServices.createIndexMapperService(clusterService.state().metaData().getIndices().get(INDEX_NAME));
    }
}
