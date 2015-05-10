package io.searchbox.cluster;

import io.searchbox.action.Action;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author cihat keser
 */
public class StateTest {

    @Test
    public void testUriGeneration() {
        Action action = new State.Builder().build();
        assertEquals("/_cluster/state", action.getURI());
    }

    @Test
    public void testUriGenerationWithOptionalFields() {
        Action action = new State.Builder()
                .withBlocks()
                .withMetadata()
                .build();
        assertEquals("/_cluster/state/blocks,metadata", action.getURI());
    }

}
