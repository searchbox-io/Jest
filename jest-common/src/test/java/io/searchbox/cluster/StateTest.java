package io.searchbox.cluster;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class StateTest {

    @Test
    public void testUriGeneration() {
        State action = new State.Builder().build();
        assertEquals("/_cluster/state", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void testUriGenerationWithOptionalFields() {
        State action = new State.Builder()
                .withBlocks()
                .withMetadata()
                .build();
        assertEquals("/_cluster/state/blocks,metadata", action.getURI(ElasticsearchVersion.UNKNOWN));
    }

}
