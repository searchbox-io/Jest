package io.searchbox.core.search.aggregation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.searchbox.core.search.aggregation.TermsAggregation.Entry;

public class TermsAggregationTermTest {

	private static String EXPECTED_KEY_VALUE = "someKeyValue";
	private static String EXPECTED_KEY_AS_STRING_VALUE = "someKeyAsStringValue";
	private static long EXPECTED_DOC_COUNT_VALUE = 100L;

	static String termObjectWithKeyAsStringField = "{\n" +
			"    \"key\": \"" + EXPECTED_KEY_VALUE + "\",\n" +
			"    \"key_as_string\": \"" + EXPECTED_KEY_AS_STRING_VALUE + "\",\n" +
			"    \"doc_count\": " + EXPECTED_DOC_COUNT_VALUE + "\n" +
			"}";

	static String termObjectWithoutKeyAsStringField = "{\n" +
			"    \"key\": \"" + EXPECTED_KEY_VALUE + "\",\n" +
			"    \"doc_count\": " + EXPECTED_DOC_COUNT_VALUE + "\n" +
			"}";

	static String termsAggregationContent = "{\n" +
			"    \"doc_count_error_upper_bound\":0,\n" +
			"    \"sum_other_doc_count\":0,\n" +
			"    \"buckets\": [\n" +
				termObjectWithKeyAsStringField +
			"    ,\n" +
				termObjectWithoutKeyAsStringField +
			"    ]\n" +
			"}";



	@Test
	public void testParseBuckets() {

		JsonObject termsAggregationJson = new Gson().fromJson(termsAggregationContent, JsonObject.class);
		TermsAggregation termsAggregation = new TermsAggregation("termsAggregation", termsAggregationJson);
		List<Entry> buckets = termsAggregation.getBuckets();
		assertNotNull(buckets);
		assertEquals(2, buckets.size());
		// Test for entry *with* key_as_value present
		Entry entryWithKeyAsString = buckets.get(0);
		assertEquals(EXPECTED_KEY_VALUE, entryWithKeyAsString.getKey());
		assertEquals(EXPECTED_KEY_AS_STRING_VALUE, entryWithKeyAsString.getKeyAsString());
		assertTrue(EXPECTED_DOC_COUNT_VALUE == entryWithKeyAsString.getCount());
		// Test for entry *without* key_as_value present
		Entry entryWithoutKeyAsString = buckets.get(1);
		assertEquals(EXPECTED_KEY_VALUE, entryWithoutKeyAsString.getKey());
		// If key_as_string wasn't populated, return key value
		assertEquals(EXPECTED_KEY_VALUE, entryWithoutKeyAsString.getKeyAsString());
		assertTrue(EXPECTED_DOC_COUNT_VALUE == entryWithoutKeyAsString.getCount());

	}

}
