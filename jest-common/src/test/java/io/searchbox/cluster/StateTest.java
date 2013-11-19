package io.searchbox.cluster;

import io.searchbox.Action;
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

}
