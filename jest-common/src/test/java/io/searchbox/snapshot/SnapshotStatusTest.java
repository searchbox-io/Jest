package io.searchbox.snapshot;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class SnapshotStatusTest {

	private String repositoryName = "leeseohoo";
	private String snapshotName = "leeseola";
	private String snapshotName2 = "kangsungjeon";

	@Test
	public void testSnapshotSingleName() {
		SnapshotStatus snapshotStatus = new SnapshotStatus.Builder(repositoryName).snapshotName(snapshotName).build();
		assertEquals("GET", snapshotStatus.getRestMethodName());
		assertEquals("/_snapshot/leeseohoo/leeseola/_status", snapshotStatus.getURI());
	}

	@Test
	public void testSnapshotMultipleNames() {
		SnapshotStatus snapshotStatus = new SnapshotStatus.Builder(repositoryName).addSnapshotNames(Arrays.asList(repositoryName, snapshotName2)).build();
		assertEquals("/_snapshot/leeseohoo/leeseohoo,kangsungjeon/_status", snapshotStatus.getURI());
	}
}
