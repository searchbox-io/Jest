package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * @author cfstout
 */
public class ScriptedMetricAggregation extends SingleValueAggregation {

    public static final String TYPE = "scripted_metric";

    public ScriptedMetricAggregation(String name, JsonObject scriptedMetricAggregation) {
        super(name, scriptedMetricAggregation);
    }

    public Double getScriptedMetric() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), TYPE);
    }

}
