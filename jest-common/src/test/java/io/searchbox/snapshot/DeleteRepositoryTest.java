package io.searchbox.snapshot;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class DeleteRepositoryTest {

	private String repositoryName = "leeseohoo";

	@Test
	public void testRepository() {
		DeleteRepository deleteRepository = new DeleteRepository.Builder(repositoryName).build();
		assertEquals("DELETE", deleteRepository.getRestMethodName());
		assertEquals("/_snapshot/leeseohoo", deleteRepository.getURI());
	}
}
