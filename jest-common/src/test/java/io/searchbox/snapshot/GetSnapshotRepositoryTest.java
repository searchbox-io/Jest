package io.searchbox.snapshot;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshotRepositoryTest {

    private String repository = "leeseohoo";
    private String repository2 = "kangsungjeon";

    @Test
    public void testRepositorySingleName() {
        GetSnapshotRepository getSnapshotRepository = new GetSnapshotRepository.Builder(repository).build();
        assertEquals("GET", getSnapshotRepository.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo", getSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testRepositoryMultipleNames() {
        GetSnapshotRepository getSnapshotRepository = new GetSnapshotRepository.Builder(repository)
                .addRepository(Arrays.asList(repository, repository2)).build();
        assertEquals("/_snapshot/leeseohoo,kangsungjeon", getSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testRepositoryAll() {
        GetSnapshotRepository getSnapshotRepository = new GetSnapshotRepository.Builder().build();
        assertEquals("/_snapshot/_all", getSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
