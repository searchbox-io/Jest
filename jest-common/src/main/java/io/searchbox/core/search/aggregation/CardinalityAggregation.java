package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class CardinalityAggregation extends Aggregation<CardinalityAggregation> {

    public static final String TYPE = "cardinality";

    private Long cardinality;

    public <T extends Aggregation> CardinalityAggregation(String name, JsonObject cardinalityAggregation) {
        super(name, cardinalityAggregation);
        cardinality = cardinalityAggregation.get(String.valueOf(VALUE)).getAsLong();
    }

    /**
     * @return Cardinality if it was found and not null, null otherwise
     */
    public Long getCardinality() {
        return cardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardinalityAggregation)) {
            return false;
        }

        CardinalityAggregation that = (CardinalityAggregation) o;

        if (cardinality != null ? !cardinality.equals(that.cardinality) : that.cardinality != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return cardinality != null ? cardinality.hashCode() : 0;
    }
}

