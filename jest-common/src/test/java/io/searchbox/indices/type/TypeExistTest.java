package io.searchbox.indices.type;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class TypeExistTest {

	@Test
	public void testBasicUriGeneration() {
		TypeExist typeExist = new TypeExist.Builder("happyprg").addType("seohoo").build();

		assertEquals("HEAD", typeExist.getRestMethodName());
		assertEquals("happyprg/seohoo", typeExist.getURI());
	}

    @Test
    public void equalsReturnsTrueForSameDestination() {
        TypeExist typeExist1 = new TypeExist.Builder("twitter").addType("tweet").build();
        TypeExist typeExist1Duplicate = new TypeExist.Builder("twitter").addType("tweet").build();

        assertEquals(typeExist1, typeExist1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentDestination() {
        TypeExist typeExist1 = new TypeExist.Builder("twitter").addType("tweet").build();
        TypeExist typeExist2 = new TypeExist.Builder("myspace").addType("page").build();

        assertNotEquals(typeExist1, typeExist2);
    }

}
