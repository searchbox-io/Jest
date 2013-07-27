package io.searchbox.cluster;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dogukan Sonmez
 */
public class NodesInfoTest {

    @Test
    public void getURIWithoutNodeAndInfo() {
        NodesInfo nodesInfo = new NodesInfo.Builder().build();
        assertEquals("/_cluster/nodes/_all", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyOneNode() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").build();
        assertEquals("/_cluster/nodes/twitter", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOneNodeAndOneInfo() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").os(true).build();
        assertEquals("/_cluster/nodes/twitter?os=true", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyOneType() {
        NodesInfo nodesInfo = new NodesInfo.Builder().os(true).build();
        assertEquals("/_cluster/nodes/_all?os=true", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleNode() {
        NodesInfo nodesInfo = new NodesInfo.Builder().addNode("twitter").addNode("searchbox").build();
        assertEquals("/_cluster/nodes/twitter,searchbox", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        NodesInfo nodesInfo = new NodesInfo.Builder().os(true).process(true).build();
        assertEquals("/_cluster/nodes/_all?process=true&os=true", nodesInfo.getURI());
    }

    @Test
    public void getURIWithMultipleNodeAndTypes() {
        NodesInfo nodesInfo = new NodesInfo.Builder()
                .addNode("twitter")
                .addNode("jest")
                .os(true)
                .process(true)
                .build();
        assertEquals("/_cluster/nodes/twitter,jest?process=true&os=true", nodesInfo.getURI());
    }

}
