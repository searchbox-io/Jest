package io.searchbox.cluster;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author Dogukan Sonmez
 */


public class NodesInfoTest {


    NodesInfo nodesInfo = new NodesInfo();

    @Test
    public void addValidNode() {
        nodesInfo.addNode("twitter");
        assertTrue(nodesInfo.isNodeExist("twitter"));
    }

    @Test
    public void addEmptyNode() {
        nodesInfo.addNode("");
        assertFalse(nodesInfo.isNodeExist(""));
        assertEquals(0, nodesInfo.nodeSize());
    }

    @Test
    public void addValidInfo() {
        nodesInfo.addInfo("os");
        assertTrue(nodesInfo.isInfoExist("os"));
    }

    @Test
    public void addEmptyInfo() {
        nodesInfo.addInfo("");
        assertFalse(nodesInfo.isInfoExist(""));
        assertEquals(0, nodesInfo.infoSize());
    }

    @Test
    public void addValidNodeCollection() {
        List<String> nodeList = new ArrayList<String>();
        nodeList.add("twitter");
        nodeList.add("nodesInfoTestbox");
        nodeList.add("JEST");
        nodesInfo.addNode(nodeList);
        assertEquals(3, nodesInfo.nodeSize());
        for (String node : nodeList) {
            assertTrue(nodesInfo.isNodeExist(node));
        }
    }

    @Test
    public void addEmptyNodeCollection() {
        List<String> nodeList = new ArrayList<String>();
        nodesInfo.addNode(nodeList);
        assertEquals(0, nodesInfo.nodeSize());
    }

    @Test
    public void addDuplicatedNodeCollection() {
        List<String> nodeList = new ArrayList<String>();
        nodeList.add("twitter");
        nodeList.add("nodesInfoTestbox");
        nodeList.add("JEST");
        nodeList.add("JEST");
        nodeList.add("JEST");
        nodesInfo.addNode(nodeList);
        assertEquals(3, nodesInfo.nodeSize());
        for (String node : nodeList) {
            assertTrue(nodesInfo.isNodeExist(node));
        }
    }

    @Test
    public void addValidInfoCollection() {
        List<String> infoList = new ArrayList<String>();
        infoList.add("os");
        infoList.add("process");
        infoList.add("http");
        nodesInfo.addInfo(infoList);
        assertEquals(3, nodesInfo.infoSize());
        for (String node : infoList) {
            assertTrue(nodesInfo.isInfoExist(node));
        }
    }

    @Test
    public void addEmptyInfoCollection() {
        List<String> typeList = new ArrayList<String>();
        nodesInfo.addInfo(typeList);
        assertEquals(0, nodesInfo.infoSize());
    }

    @Test
    public void addDuplicatedInfoCollection() {
        List<String> infoList = new ArrayList<String>();
        infoList.add("os");
        infoList.add("os");
        infoList.add("process");
        infoList.add("process");
        infoList.add("process");
        nodesInfo.addInfo(infoList);
        assertEquals(2, nodesInfo.infoSize());
        for (String node : infoList) {
            assertTrue(nodesInfo.isInfoExist(node));
        }
    }

    @Test
    public void clearAllInfo() {
        List<String> infoList = new ArrayList<String>();
        infoList.add("tweet");
        infoList.add("io");
        infoList.add("Java");
        infoList.add("c");
        infoList.add("groovy");
        nodesInfo.addInfo(infoList);
        assertEquals(5, nodesInfo.infoSize());
        nodesInfo.clearAllInfo();
        assertEquals(0, nodesInfo.infoSize());
    }

    @Test
    public void clearAllNode() {
        List<String> nodeList = new ArrayList<String>();
        nodeList.add("twitter");
        nodeList.add("nodesInfoTestbox");
        nodeList.add("JEST");
        nodeList.add("groovy");
        nodeList.add("java");
        nodesInfo.addNode(nodeList);
        assertEquals(5, nodesInfo.nodeSize());
        nodesInfo.clearAllNode();
        assertEquals(0, nodesInfo.nodeSize());
    }

    @Test
    public void removeInfo() {
        nodesInfo.addInfo("process");
        assertTrue(nodesInfo.isInfoExist("process"));
        assertEquals(1, nodesInfo.infoSize());
        nodesInfo.removeInfo("process");
        assertEquals(0, nodesInfo.infoSize());
    }

    @Test
    public void removeNode() {
        nodesInfo.addNode("twitter");
        assertTrue(nodesInfo.isNodeExist("twitter"));
        assertEquals(1, nodesInfo.nodeSize());
        nodesInfo.removeNode("twitter");
        assertEquals(0, nodesInfo.nodeSize());
    }

    @Test
    public void getURIWithoutNodeAndInfo() {
        assertEquals("_cluster/nodes", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyOneNode() {
        nodesInfo.addNode("twitter");
        assertEquals("_cluster/nodes/twitter", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOneNodeAndOneInfo() {
        nodesInfo.addNode("twitter");
        nodesInfo.addInfo("os");
        assertEquals("_cluster/nodes/twitter?os=true", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyOneType() {
        nodesInfo.addInfo("os");
        assertEquals("_cluster/nodes?os=true", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleNode() {
        nodesInfo.addNode("twitter");
        nodesInfo.addNode("searchbox");
        assertEquals("_cluster/nodes/twitter,searchbox", nodesInfo.getURI());
    }

    @Test
    public void getURIWithOnlyMultipleType() {
        nodesInfo.addInfo("os");
        nodesInfo.addInfo("process");
        assertEquals("_cluster/nodes?os=true&process=true", nodesInfo.getURI());
    }

    @Test
    public void getURIWithMultipleNodeAndTypes() {
        nodesInfo.addNode("twitter");
        nodesInfo.addNode("jest");
        nodesInfo.addInfo("os");
        nodesInfo.addInfo("process");
        assertEquals("_cluster/nodes/twitter,jest?os=true&process=true", nodesInfo.getURI());
    }

    @Test
    public void createQueryStringForNode() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("twitter");
        set.add("searchbox");
        assertEquals("twitter,searchbox",nodesInfo.concatenateString(set));
    }

    @Test
    public void createQueryStringForInfo() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add("os");
        set.add("process");
        assertEquals("os=true&process=true",nodesInfo.buildQueryStringForInfo(set));
    }

}
