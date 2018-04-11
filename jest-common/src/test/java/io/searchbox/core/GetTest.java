package io.searchbox.core;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */


public class GetTest {

    @Test
    public void getDocument() {
        Get get =  new Get.Builder("twitter", "1").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1", get.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
