package io.searchbox.core.search.facet;

import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class StatisticalFacet extends Facet {

    public static final String TYPE = "statistical";

    private Long count;
    private Double total;
    private Double sumOfSquares;
    private Double mean;
    private Double min;
    private Double max;
    private Double variance;
    private Double stdDeviation;

    public StatisticalFacet(String name, JsonObject statisticalFacet) {
        this.name = name;
        this.count =  statisticalFacet.get("count").getAsLong();
        this.total = statisticalFacet.get("total").getAsDouble();
        this.sumOfSquares = statisticalFacet.get("sum_of_squares").getAsDouble();
        this.mean = statisticalFacet.get("mean").getAsDouble();
        this.min = statisticalFacet.get("min").getAsDouble();
        this.max =  statisticalFacet.get("max").getAsDouble();
        this.variance = statisticalFacet.get("variance").getAsDouble();
        this.stdDeviation = statisticalFacet.get("std_deviation").getAsDouble();
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
