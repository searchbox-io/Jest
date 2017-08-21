package io.searchbox.search.aggregation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation.Entry;
import io.searchbox.core.search.aggregation.TopHitsAggregation;

public class ChildAggregationTest {

	// @formatter:off
    String json = "{\n" +
            "    \"_shards\":{\n" +
            "        \"total\" : 5,\n" +
            "        \"successful\" : 5,\n" +
            "        \"failed\" : 0\n" +
            "    },\n" +
            "    \"hits\":{\n" +
            "        \"total\" : 1,\n" +
            "	     \"hits\": []\n" +
            "    },\n" +
            "    \"aggregations\": {\n" +
            "        \"childtest\": {\n" +
            "            \"test\": {\n" +
            "                \"doc_count_error_upper_bound\": 1,\n" +
            "                \"sum_other_doc_count\": 2,\n" +
            "                \"buckets\": [\n" +
            "                    {\n" +
            "                        \"key\": \"keyTest1\",\n" +
            "                        \"doc_count\": 2,\n" +
            "                        \"top_hits_test\": {\n" +
            "                            \"hits\": {\n" +
            "                                \"total\": 2,\n" +
            "                                \"max_score\": 1.22222,\n" +
            "                                \"hits\": [\n" +
            "                                    {\n" +
            "                                        \"_index\": \"indexName\",\n" +
            "                                        \"_type\": \"typeName\",\n" +
            "                                        \"_id\": \"testId1\",\n" +
            "                                        \"_score\": 1.11111,\n" +
            "                                        \"_source\": {\n" +
            "                                            \"field1\": \"field1Content\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"_index\": \"indexName\",\n" +
            "                                        \"_type\": \"typeName\",\n" +
            "                                        \"_id\": \"testId2\",\n" +
            "                                        \"_score\": 1.0000,\n" +
            "                                        \"_source\": {\n" +
            "                                            \"field1\": \"field2Content\"\n" +
            "                                        }\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            }\n" +
            "                        }\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"key\": \"keyTest2\",\n" +
            "                        \"doc_count\": 2,\n" +
            "                        \"top_hits_test\": {\n" +
            "                            \"hits\": {\n" +
            "                                \"total\": 2,\n" +
            "                                \"max_score\": 1.22222,\n" +
            "                                \"hits\": [\n" +
            "                                    {\n" +
            "                                        \"_index\": \"indexName\",\n" +
            "                                        \"_type\": \"typeName\",\n" +
            "                                        \"_id\": \"testId21\",\n" +
            "                                        \"_score\": 1.11111,\n" +
            "                                        \"_source\": {\n" +
            "                                            \"field1\": \"field21Content\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"_index\": \"indexName\",\n" +
            "                                        \"_type\": \"typeName\",\n" +
            "                                        \"_id\": \"testId22\",\n" +
            "                                        \"_score\": 1.0000,\n" +
            "                                        \"_source\": {\n" +
            "                                            \"field1\": \"field22Content\"\n" +
            "                                        }\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" + 
            "}";
	// @formatter:on

	@Test
	public void testGetMaxScoreWhenMissingWithChildren() {
		System.out.println(json);
		
		SearchResult searchResult = new SearchResult(new Gson());
		searchResult.setSucceeded(true);
		searchResult.setJsonString(json);
		searchResult.setJsonObject(new JsonParser().parse(json).getAsJsonObject());
		searchResult.setPathToResult("hits/hits/_source");

		Float maxScore = searchResult.getMaxScore();
		assertNull(maxScore);
		
		ChildrenAggregation childAgg = searchResult.getAggregations().getChildrenAggregation("childtest");
		assertNotNull(childAgg);

		TermsAggregation termsAgg = childAgg.getTermsAggregation("test");

		assertEquals(new Long(1), termsAgg.getDocCountErrorUpperBound());
		assertEquals(new Long(2), termsAgg.getSumOtherDocCount());

		List<Entry> termBuckets = termsAgg.getBuckets();

		for (Entry entry : termBuckets) {
			TopHitsAggregation topHitAgg = entry.getTopHitsAggregation("top_hits_test");

			List<String> hits = topHitAgg.getSourceAsStringList();

			assertEquals(2, hits.size());
		}
	}
}
