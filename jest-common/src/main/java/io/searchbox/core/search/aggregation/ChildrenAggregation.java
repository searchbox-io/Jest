package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

public class ChildrenAggregation extends MetricAggregation {

	protected String name;
	protected JsonObject jsonRoot;
	public static final String TYPE = "child";

	public ChildrenAggregation(String name, JsonObject childrenAggregation) {

		super(name, childrenAggregation);
	}
}
