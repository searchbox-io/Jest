package io.searchbox.core.search.aggregation;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.searchbox.core.SearchResult;

public class TopHitsAggregation extends SearchResult {

	protected String name;
	protected JsonObject jsonRoot;
	public static final String TYPE = "top_hits";

	public TopHitsAggregation(String name, JsonObject topHitAggregation) {
		this(name, topHitAggregation, null);
	}
	
	public TopHitsAggregation(String name, JsonObject topHitAggregation, Gson gson) {
		super(gson == null ? new Gson() : gson);
		this.name = name;

		this.setSucceeded(true);
		this.setJsonObject(topHitAggregation);
		this.setPathToResult("hits/hits/_source");
	}

	public String getName() {
		return name;
	}
}
