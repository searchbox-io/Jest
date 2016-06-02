package io.searchbox.snapshot;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class GetSnapshotTest {

	private String repository = "kangsungjeon";
	private String snapshot = "leeseohoo";
	private String snapshot2 = "kangsungjeon";

	@Test
	public void testSnapshotSingleName() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).build();
		assertEquals("GET", getSnapshotRepository.getRestMethodName());
		assertEquals("/_snapshot/kangsungjeon", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotMultipleNames() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).addSnapshot(Arrays.asList(snapshot, snapshot2)).build();
		assertEquals("/_snapshot/kangsungjeon/leeseohoo,kangsungjeon", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotAll() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).addSnapshot(snapshot).addSnapshot(Arrays.asList(snapshot2)).all(true).build();
		assertEquals("/_snapshot/kangsungjeon/_all", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotCurrent() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).current(true).all(true).build();
		assertEquals("/_snapshot/kangsungjeon/_current", getSnapshotRepository.getURI());
	}

	@Test
	public void testSnapshotAllOnly() {
		GetSnapshot getSnapshotRepository = new GetSnapshot.Builder(repository).all(true).build();
		assertEquals("/_snapshot/kangsungjeon/_all", getSnapshotRepository.getURI());
	}

}
