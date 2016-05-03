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
	super(new Gson());
	this.name = name;
	
	this.setSucceeded(true);
	this.setJsonObject(topHitAggregation);
	//this.setJsonString(topHitAggregation.getAsString());
	this.setPathToResult("hits/hits/_source");
    }

    public String getName() {
	return name;
    }
}
