package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.AVG;
import static io.searchbox.core.search.aggregation.AggregationField.COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.MAX;
import static io.searchbox.core.search.aggregation.AggregationField.MIN;
import static io.searchbox.core.search.aggregation.AggregationField.SUM;

/**
 * @author cfstout
 */
public class StatsAggregation<T extends StatsAggregation> extends Aggregation<T> {

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatsAggregation that = (StatsAggregation) o;

        if (avg != null ? !avg.equals(that.avg) : that.avg != null) {
            return false;
        }
        if (!count.equals(that.count)) {
            return false;
        }
        if (max != null ? !max.equals(that.max) : that.max != null) {
            return false;
        }
        if (min != null ? !min.equals(that.min) : that.min != null) {
            return false;
        }
        if (sum != null ? !sum.equals(that.sum) : that.sum != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = count.hashCode();
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (avg != null ? avg.hashCode() : 0);
        result = 31 * result + (sum != null ? sum.hashCode() : 0);
        return result;
    }
}
