package io.searchbox.snapshot;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshotTest {

    private String repository = "leeseohoo";
    private String snapshot = "leeseola";

    @Test
    public void testSnapshot() {
        DeleteSnapshot deleteSnapshot = new DeleteSnapshot.Builder(repository, snapshot).build();
        assertEquals("DELETE", deleteSnapshot.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo/leeseola", deleteSnapshot.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
