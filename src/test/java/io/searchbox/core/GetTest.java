package io.searchbox.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class GetTest {

    @Test
    public void getDocument() {
        Get get =  new Get.Builder("1").index("twitter").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("twitter/tweet/1", get.getURI());
    }

    @Test
    public void getDocumentWithDefaultIndex() {
        Get get = new Get.Builder("1").type("tweet").build();
        assertEquals("GET", get.getRestMethodName());
        assertEquals("<jesttempindex>/tweet/1", get.getURI());
    }

    @Test
    public void getDocumentWithDefaultIndexAndType() {
        Get get = new Get.Builder("1").build();
        assertEquals("GET", get.getRestMethodName());
    }


}
