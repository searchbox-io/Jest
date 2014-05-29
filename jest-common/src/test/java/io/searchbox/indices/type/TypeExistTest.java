package io.searchbox.indices.type;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class TypeExistTest {

	private static final String REQUEST_METHOD = "HEAD";
	private static final String INDEX_NAME = "happyprg";
	private static final String INDEX_TYPE_NAME = "seohoo";

	@Test
	public void typeExists() {

		TypeExist typeExist = new TypeExist.Builder(INDEX_NAME).addType(INDEX_TYPE_NAME).build();

		assertEquals(REQUEST_METHOD, typeExist.getRestMethodName());
		assertEquals(INDEX_NAME + "/" + INDEX_TYPE_NAME, typeExist.getURI());
	}
}
