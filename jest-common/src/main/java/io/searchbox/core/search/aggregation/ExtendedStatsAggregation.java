package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.STD_DEVIATION;
import static io.searchbox.core.search.aggregation.AggregationField.SUM_OF_SQUARES;
import static io.searchbox.core.search.aggregation.AggregationField.VARIANCE;

/**
 * @author cfstout
 */
public class ExtendedStatsAggregation extends StatsAggregation<ExtendedStatsAggregation> {

    private Double sumOfSquares;
    private Double variance;
    private Double stdDeviation;

    public <T extends Aggregation> ExtendedStatsAggregation(String name, JsonObject extendedStatsAggregation) {
        super(name, extendedStatsAggregation);
        this.sumOfSquares = !extendedStatsAggregation.has(String.valueOf(SUM_OF_SQUARES)) || extendedStatsAggregation.get(String.valueOf(SUM_OF_SQUARES)).isJsonNull() ?
                null : extendedStatsAggregation.get(String.valueOf(SUM_OF_SQUARES)).getAsDouble();
        this.variance = !extendedStatsAggregation.has(String.valueOf(VARIANCE)) || extendedStatsAggregation.get(String.valueOf(VARIANCE)).isJsonNull() ?
                null : extendedStatsAggregation.get(String.valueOf(VARIANCE)).getAsDouble();
        this.stdDeviation = !extendedStatsAggregation.has(String.valueOf(STD_DEVIATION)) || extendedStatsAggregation.get(String.valueOf(STD_DEVIATION)).isJsonNull() ?
                null : extendedStatsAggregation.get(String.valueOf(STD_DEVIATION)).getAsDouble();
    }

    /**
     * @return Sum of Squares for the aggregated data if found, null otherwise
     */
    public Double getSumOfSquares() {
        return sumOfSquares;
    }

    /**
     * @return Variance of the aggregated data if found, null otherwise
     */
    public Double getVariance() {
        return variance;
    }

    /**
     * @return Standard deviation of the aggregated data if found, null otherwise
     */
    public Double getStdDeviation() {
        return stdDeviation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ExtendedStatsAggregation that = (ExtendedStatsAggregation) o;

        if (stdDeviation != null ? !stdDeviation.equals(that.stdDeviation) : that.stdDeviation != null) {
            return false;
        }
        if (sumOfSquares != null ? !sumOfSquares.equals(that.sumOfSquares) : that.sumOfSquares != null) {
            return false;
        }
        if (variance != null ? !variance.equals(that.variance) : that.variance != null) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (sumOfSquares != null ? sumOfSquares.hashCode() : 0);
        result = 31 * result + (variance != null ? variance.hashCode() : 0);
        result = 31 * result + (stdDeviation != null ? stdDeviation.hashCode() : 0);
        return result;
    }
}
