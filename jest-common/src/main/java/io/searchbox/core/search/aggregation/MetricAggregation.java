package io.searchbox.core.search.aggregation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cfstout
 */
public abstract class MetricAggregation extends Aggregation {

    public MetricAggregation(String name, JsonObject root) {
        super(name, root);
    }

    /**
     * @param nameToTypeMap a map of aggNames to their expected type (extension of Aggregation class)
     * @return A list of aggregation objects for the provided name:type pairs if the name can be found in the root json object
     * @exception java.lang.RuntimeException if no constructor found for an expected type in the map
     */
    @SuppressWarnings("unchecked")
    public List<Aggregation> getAggregations(Map<String, Class> nameToTypeMap) {
        List<Aggregation> aggregations = new ArrayList<Aggregation>();
        for (String nameCandidate : nameToTypeMap.keySet()) {
            if (jsonRoot.has(nameCandidate)) {
                try {
                    Class type = nameToTypeMap.get(nameCandidate);
                    Constructor c = type.getConstructor(String.class, JsonObject.class);
                    aggregations.add((Aggregation)c.newInstance(nameCandidate, jsonRoot.getAsJsonObject(nameCandidate)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return aggregations;
    }

    /**
     * @param aggName Name of the desired aggregation
     * @param aggType Extension of Aggregation class expected as return type
     * @return Aggregation of type T if aggName can be found in aggregation json or null otherwise
     * @exception java.lang.RuntimeException if no constructor exists for provided aggType
     */
    public <T extends Aggregation> T getAggregation(String aggName, Class<T> aggType) {
        if(jsonRoot.has(aggName)) {
            try {
                Constructor<T> c = aggType.getConstructor(String.class, JsonObject.class);
                return c.newInstance(aggName, jsonRoot.getAsJsonObject(aggName));
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * @param aggName Name of the AvgAggregation
     * @return a new AvgAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public AvgAggregation getAvgAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new AvgAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the CardinalityAggregation
     * @return a new CardinalityAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public CardinalityAggregation getCardinalityAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new CardinalityAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the CompositeAggregation
     * @return a new CompositeAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public CompositeAggregation getCompositeAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new CompositeAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the DateHistogramAggregation
     * @return a new DateHistogramAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public DateHistogramAggregation getDateHistogramAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new DateHistogramAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the DateRangeAggregation
     * @return a new DateRangeAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public DateRangeAggregation getDateRangeAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new DateRangeAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the ExtendedStatsAggregation
     * @return a new ExtendedStatsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public ExtendedStatsAggregation getExtendedStatsAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new ExtendedStatsAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the FilterAggregation
     * @return a new FilterAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public FilterAggregation getFilterAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new FilterAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the FiltersAggregation
     * @return a new FiltersAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public FiltersAggregation getFiltersAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new FiltersAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the GeoBoundsAggregation
     * @return a new GeoBoundsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public GeoBoundsAggregation getGeoBoundsAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new GeoBoundsAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the GeoDistanceAggregation
     * @return a new GeoDistanceAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public GeoDistanceAggregation getGeoDistanceAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new GeoDistanceAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the GeohashGridAggregation
     * @return a new GeohashGridAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public GeoHashGridAggregation getGeohashGridAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new GeoHashGridAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the HistogramAggregation
     * @return a new HistogramAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public HistogramAggregation getHistogramAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new HistogramAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the Ipv4RangeAggregation
     * @return a new Ipv4RangeAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public Ipv4RangeAggregation getIpv4RangeAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new Ipv4RangeAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the MaxAggregation
     * @return a new MaxAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public MaxAggregation getMaxAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new MaxAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the MinAggregation
     * @return a new MinAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public MinAggregation getMinAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new MinAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the MissingAggregation
     * @return a new MissingAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public MissingAggregation getMissingAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new MissingAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the PercentileRanksAggregation
     * @return a new PercentileRanksAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public PercentileRanksAggregation getPercentileRanksAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new PercentileRanksAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the PercentilesAggregation
     * @return a new PercentilesAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public PercentilesAggregation getPercentilesAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new PercentilesAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the RangeAggregation
     * @return a new RangeAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public RangeAggregation getRangeAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new RangeAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the ScriptedMetricAggregation
     * @return a new ScriptedMetricAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public ScriptedMetricAggregation getScriptedMetricAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new ScriptedMetricAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the SignificantTermsAggregation
     * @return a new SignificantTermsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public SignificantTermsAggregation getSignificantTermsAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new SignificantTermsAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the StatsAggregation
     * @return a new StatsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public StatsAggregation getStatsAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new StatsAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the SumAggregation
     * @return a new SumAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public SumAggregation getSumAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new SumAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the TermsAggregation
     * @return a new TermsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public TermsAggregation getTermsAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new TermsAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }

    /**
     * @param aggName Name of the ValueCountAggregation
     * @return a new ValueCountAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public ValueCountAggregation getValueCountAggregation(String aggName) {
        return jsonRoot.has(aggName) ? new ValueCountAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }
    
    /**
     * @param aggName Name of the TopHitsAggregation
     * @return a new TopHitsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public TopHitsAggregation getTopHitsAggregation(String aggName) {
    		return jsonRoot.has(aggName) ? new TopHitsAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
    }
    
    /**
     * @param aggName Name of the TopHitsAggregation
     * @param gson Custom Gson parser for top hit results
     * @return a new TopHitsAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */
    public TopHitsAggregation getTopHitsAggregation(String aggName, Gson gson) {
    		return jsonRoot.has(aggName) ? new TopHitsAggregation(aggName, jsonRoot.getAsJsonObject(aggName), gson) : null;
    }

    
    /**
     * @param aggName Name of the ChildrenAggregation
     * @return a new ChildrenAggregation object if aggName is found within sub-aggregations of current aggregation level or null if not found
     */

    public ChildrenAggregation getChildrenAggregation(String aggName) {
		return jsonRoot.has(aggName) ? new ChildrenAggregation(aggName, jsonRoot.getAsJsonObject(aggName)) : null;
}

}
