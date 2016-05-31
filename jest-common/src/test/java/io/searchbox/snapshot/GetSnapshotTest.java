package io.searchbox.snapshot;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshotTest {

	private String repositoryName = "kangsungjeon";
	private String snapshotName = "leeseohoo";
	private String snapshotName2 = "kangsungjeon";

	@Test
	public void testSnapshotSingleName() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repositoryName).build();
		assertEquals("GET", getSnapshotRepository.getRestMethodName());
		assertEquals("/_snapshot/kangsungjeon", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotMultipleNames() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repositoryName).addSnapshotNames(Arrays.asList(snapshotName, snapshotName2)).build();
		assertEquals("/_snapshot/kangsungjeon/leeseohoo,kangsungjeon", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotAll() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repositoryName).addSnapshotNames(Arrays.asList(snapshotName, snapshotName2)).all(true).build();
		assertEquals("/_snapshot/kangsungjeon/_all", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotCurrent() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repositoryName).current(true).all(true).build();
		assertEquals("/_snapshot/kangsungjeon/_current", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotAllOnly() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repositoryName).all(true).build();
		assertEquals("/_snapshot/kangsungjeon/_all", getSnapshotRepository.getURI());
	}

}
