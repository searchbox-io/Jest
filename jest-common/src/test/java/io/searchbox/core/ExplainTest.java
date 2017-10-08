package io.searchbox.core;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Dogukan Sonmez
 */
public class ExplainTest {

    @Test
    public void explain() {
        Explain explain = new Explain.Builder("twitter", "tweet", "1", "query").build();
        assertEquals("GET", explain.getRestMethodName());
        assertEquals("twitter/tweet/1/_explain", explain.getURI());
        assertEquals("query", explain.getData(null));
    }

    @Test
    public void equals(){
        Explain explainUserKramer = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"kramer\"}").build();
        Explain explainUserKramerDuplicate = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"kramer\"}").build();

        assertEquals(explainUserKramer, explainUserKramerDuplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries(){
        Explain explainUserKramer = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"kramer\"}").build();
        Explain explainUserJerry = new Explain.Builder("twitter", "tweet", "1", "{\"user\":\"jerry\"}").build();

        assertNotEquals(explainUserKramer, explainUserJerry);
    }

}
