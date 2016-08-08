package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static io.searchbox.core.search.aggregation.AggregationField.VALUE;

/**
 * @author cfstout
 */
public class CardinalityAggregation extends MetricAggregation {

    public static final String TYPE = "cardinality";

    private Long cardinality;

    public CardinalityAggregation(String name, JsonObject cardinalityAggregation) {
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

        CardinalityAggregation rhs = (CardinalityAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(cardinality, rhs.cardinality)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(cardinality)
                .toHashCode();
    }
}

