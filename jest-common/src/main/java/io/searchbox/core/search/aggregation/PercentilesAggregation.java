package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author cfstout
 */
public class PercentilesAggregation extends MetricAggregation {

    public static final String TYPE = "percentiles";

    private Map<String, Double> percentiles = new HashMap<String, Double>();

    public PercentilesAggregation(String name, JsonObject percentilesAggregation) {
        super(name, percentilesAggregation);
        parseSource(percentilesAggregation.getAsJsonObject(String.valueOf(AggregationField.VALUES)));
    }

    private void parseSource(JsonObject source) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            if(!(Double.isNaN(entry.getValue().getAsDouble()))) {
                percentiles.put(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
    }

    public Map<String, Double> getPercentiles() {
        return percentiles;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        PercentilesAggregation rhs = (PercentilesAggregation) obj;
        return super.equals(obj) && Objects.equals(percentiles, rhs.percentiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), percentiles);
    }

}
