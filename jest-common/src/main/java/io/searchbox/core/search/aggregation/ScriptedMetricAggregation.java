package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class ScriptedMetricAggregation extends SingleValueAggregation<ScriptedMetricAggregation> {

    public static final String TYPE = "scripted_metric";

    public ScriptedMetricAggregation(String name, JsonObject scriptedMetricAggregation) {
        super(name, scriptedMetricAggregation);
    }

    public Double getScriptedMetric() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScriptedMetricAggregation)) {
            return false;
        }
        return super.equals(o);
    }
}
