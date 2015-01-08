package io.searchbox.core.gson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author happyprg
 */

public class DocumentExclusionStrategyTest {

	public final SampleObjectForTest original = new SampleObjectForTest(5, "iloveLeeSeoHoo", 1234);

	@Test
	public void exclude() {

		Gson exclusionStrategyAwareGson = new GsonBuilder().setExclusionStrategies(DocumentExclusionStrategy.create())
															.create();

		String expected = exclusionStrategyAwareGson.toJson(original);
		String actual = "{\"stringField\":\"iloveLeeSeoHoo\",\"longField\":1234}"; //ignored annotatedField
		assertEquals(expected, actual);
	}

	@Test
	public void excludeWithType() {

		Gson exclusionStrategyAwareGson = new GsonBuilder().setExclusionStrategies(DocumentExclusionStrategy.createWithSkipType(String.class))
															.create();

		String expected = exclusionStrategyAwareGson.toJson(original);
		String actual = "{\"longField\":1234}"; //ignored annotatedField, skipType
		assertEquals(expected, actual);
	}

	public static class SampleObjectForTest {

		@Exclude
		private final int annotatedField;
		private final String stringField;
		private final long longField;

		public SampleObjectForTest(int annotatedField, String stringField, long longField) {
			this.annotatedField = annotatedField;
			this.stringField = stringField;
			this.longField = longField;
		}
	}
}
