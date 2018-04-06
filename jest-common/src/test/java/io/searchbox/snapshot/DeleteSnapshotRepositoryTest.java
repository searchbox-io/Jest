package io.searchbox.snapshot;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshotRepositoryTest {
    @Test
    public void testRepository() {
        String repository = "leeseohoo";

        DeleteSnapshotRepository deleteSnapshotRepository = new DeleteSnapshotRepository.Builder(repository).build();
        assertEquals("DELETE", deleteSnapshotRepository.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo", deleteSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
