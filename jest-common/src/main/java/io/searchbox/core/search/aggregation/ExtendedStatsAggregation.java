package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;


public class ExtendedStatsAggregation extends Aggregation {

    private Long count;
    private Double total;
    private Double sumOfSquares;
    private Double mean;
    private Double min;
    private Double max;
    private Double variance;
    private Double stdDeviation;

    public ExtendedStatsAggregation(String name, JsonObject extendedStatsAggregation) {
        this.name = name;
        this.count =  extendedStatsAggregation.get("count").getAsLong();
        this.total = extendedStatsAggregation.get("total").getAsDouble();
        this.sumOfSquares = extendedStatsAggregation.get("sum_of_squares").getAsDouble();
        this.mean = extendedStatsAggregation.get("mean").getAsDouble();
        this.min = extendedStatsAggregation.get("min").getAsDouble();
        this.max =  extendedStatsAggregation.get("max").getAsDouble();
        this.variance = extendedStatsAggregation.get("variance").getAsDouble();
        this.stdDeviation = extendedStatsAggregation.get("std_deviation").getAsDouble();
    }

    public Long getCount() {
        return count;
    }

    public Double getTotal() {
        return total;
    }

    public Double getSumOfSquares() {
        return sumOfSquares;
    }

    public Double getMean() {
        return mean;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getVariance() {
        return variance;
    }

    public Double getStdDeviation() {
        return stdDeviation;
    }
}
