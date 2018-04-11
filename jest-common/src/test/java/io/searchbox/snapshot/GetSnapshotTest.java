package io.searchbox.snapshot;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshotTest {

    private String repository = "kangsungjeon";
    private String snapshot = "leeseohoo";
    private String snapshot2 = "kangsungjeon";

    @Test
    public void testSnapshotMultipleNames() {
        GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).addSnapshot(Arrays.asList(snapshot, snapshot2)).build();
        assertEquals("/_snapshot/kangsungjeon/leeseohoo,kangsungjeon", getSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testSnapshotAll() {
        GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).build();
        assertEquals("/_snapshot/kangsungjeon/_all", getSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }

}
