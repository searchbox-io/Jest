package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class StatsAggregation extends MetricAggregation {

    public static final String TYPE = "stats";

    private Long count;
    private Double min;
    private Double max;
    private Double avg;
    private Double sum;

    public StatsAggregation(String name, JsonObject statsAggregation) {
        super(name, statsAggregation);
        this.count = statsAggregation.get(String.valueOf(COUNT)).getAsLong();
        this.min = !statsAggregation.has(String.valueOf(MIN)) || statsAggregation.get(String.valueOf(MIN)).isJsonNull() ?
            null : statsAggregation.get(String.valueOf(MIN)).getAsDouble();
        this.max = !statsAggregation.has(String.valueOf(MAX)) || statsAggregation.get(String.valueOf(MAX)).isJsonNull() ?
                null : statsAggregation.get(String.valueOf(MAX)).getAsDouble();
        this.avg = !statsAggregation.has(String.valueOf(AVG)) || statsAggregation.get(String.valueOf(AVG)).isJsonNull() ?
                null : statsAggregation.get(String.valueOf(AVG)).getAsDouble();
        this.sum = !statsAggregation.has(String.valueOf(SUM)) || statsAggregation.get(String.valueOf(SUM)).isJsonNull() ?
                null : statsAggregation.get(String.valueOf(SUM)).getAsDouble();
    }

    public Long getCount() {
        return count;
    }

    /**
     * @return Min if it was found and not null, null otherwise
     */
    public Double getMin() {
        return min;
    }

    /**
     * @return Max if it was found and not null, null otherwise
     */
    public Double getMax() {
        return max;
    }

    /**
     * @return Avg if it was found and not null, null otherwise
     */
    public Double getAvg() {
        return avg;
    }

    /**
     * @return Sum if it was found and not null, null otherwise
     */
    public Double getSum() {
        return sum;
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

        StatsAggregation rhs = (StatsAggregation) obj;
        return super.equals(obj)
                && Objects.equals(count, rhs.count)
                && Objects.equals(min, rhs.min)
                && Objects.equals(max, rhs.max)
                && Objects.equals(avg, rhs.avg)
                && Objects.equals(sum, rhs.sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                count,
                avg,
                max,
                min,
                sum);
    }
}
