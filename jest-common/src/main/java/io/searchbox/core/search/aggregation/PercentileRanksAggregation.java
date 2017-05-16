package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.VALUES;

/**
 * @author cfstout
 */
public class PercentileRanksAggregation extends MetricAggregation {

    public static final String TYPE = "percentile_ranks";

    private Map<String, Double> percentileRanks = new HashMap<String, Double>();

    public PercentileRanksAggregation(String name, JsonObject percentilesAggregation) {
        super(name, percentilesAggregation);
        parseSource(percentilesAggregation.getAsJsonObject(String.valueOf(VALUES)));
    }

    private void parseSource(JsonObject source) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            if (!(Double.isNaN(entry.getValue().getAsDouble()))) {
                percentileRanks.put(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
    }

    public Map<String, Double> getPercentileRanks() {
        return percentileRanks;
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

        PercentileRanksAggregation rhs = (PercentileRanksAggregation) obj;
        return super.equals(obj) && Objects.equals(percentileRanks, rhs.percentileRanks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), percentileRanks);
    }

}
