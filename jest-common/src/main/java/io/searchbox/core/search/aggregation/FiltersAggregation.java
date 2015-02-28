package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;

/**
 * @author cfstout
 */
public class FiltersAggregation extends Aggregation {

    public static final String TYPE = "filters";

    private Map<String, Long> counts;
    private List<Long> countList;

    public FiltersAggregation(String name, JsonObject filtersAggregation) {
        super(name, filtersAggregation);
        counts = new HashMap<String, Long>();
        countList = new ArrayList<Long>();
        if (filtersAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            int elementNumber = 0;
            for (JsonElement bucket : filtersAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
                Long count = bucket.getAsJsonObject().get(String.valueOf(DOC_COUNT)).getAsLong();
                counts.put("filter" + Integer.toString(elementNumber++), count);
                countList.add(count);
            }
        } else { // returned as a json object
            for (Map.Entry<String, JsonElement> bucket: filtersAggregation.get(String.valueOf(BUCKETS)).getAsJsonObject().entrySet()) {
                Long count = bucket.getValue().getAsJsonObject().get(String.valueOf(DOC_COUNT)).getAsLong();
                counts.put(bucket.getKey(), count);
                countList.add(count);
            }
        }
    }

    /**
     * Method for getting counts when filters when using anonymous filtering
     * @return A list of counts in the order that the filters were passed in
     */
    public List<Long> getCountList() {
        return countList;
    }

    /**
     * Method for getting counts using named filters
     * @return A map filter names to associated counts
     */
    public Map<String, Long> getCounts() {
        return counts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FiltersAggregation)) {
            return false;
        }

        FiltersAggregation rhs = (FiltersAggregation) o;
        return new EqualsBuilder()
                .append(getCountList(), rhs.getCountList())
                .append(getCounts(), rhs.getCounts())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCountList())
                .append(getCounts())
                .toHashCode();
    }
}

