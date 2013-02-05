package io.searchbox.core.search.facet;

import java.util.Map;

/**
 * @author ferhat
 */
public class StatisticalFacet {

    public static final String TYPE = "statistical";

    private String name;

    private Long count;
    private Double total;
    private Double sumOfSquares;
    private Double mean;
    private Double min;
    private Double max;
    private Double variance;
    private Double stdDeviation;

    public StatisticalFacet(String name, Map statisticalFacet) {
        this.name = name;
        this.count = ((Double) statisticalFacet.get("count")).longValue();
        this.total = (Double) statisticalFacet.get("total");
        this.sumOfSquares = (Double) statisticalFacet.get("sum_of_squares");
        this.mean = (Double) statisticalFacet.get("mean");
        this.min = (Double) statisticalFacet.get("min");
        this.max = (Double) statisticalFacet.get("max");
        this.variance = (Double) statisticalFacet.get("variance");
        this.stdDeviation = (Double) statisticalFacet.get("std_deviation");
    }

    public String getName() {
        return name;
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
