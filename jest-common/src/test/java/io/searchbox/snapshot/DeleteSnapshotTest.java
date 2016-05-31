package io.searchbox.snapshot;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteSnapshotTest {

	private String repositoryName = "leeseohoo";
	private String snapshotName = "leeseola";

	@Test
	public void testSnapshot() {
		DeleteSnapshot deleteSnapshot = new DeleteSnapshot.Builder(repositoryName).snapshotName(snapshotName).build();
		assertEquals("DELETE", deleteSnapshot.getRestMethodName());
		assertEquals("/_snapshot/leeseohoo/leeseola", deleteSnapshot.getURI());
	}
}
