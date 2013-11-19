package io.searchbox.core.search.facet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ferhat
 */
public class RangeFacet extends Facet {

    public static final String TYPE = "range";

    private List<Range> ranges;

    public RangeFacet(String name, JsonObject rangeFacet) {
        this.name = name;
        ranges = new ArrayList<Range>();
        for (JsonElement termv : rangeFacet.get("ranges").getAsJsonArray()) {
            JsonObject term = (JsonObject) termv;
            Range range = new Range(
                    term.has("from") ? term.get("from").getAsDouble() : null,
                    term.has("to") ? term.get("to").getAsDouble() : null,
                    term.has("count") ? term.get("count").getAsLong() : null,
                    term.has("total_count") ? term.get("total_count").getAsLong() : null,
                    term.has("total") ? term.get("total").getAsDouble() : null,
                    term.has("min") ? term.get("min").getAsDouble() : null,
                    term.has("max") ? term.get("max").getAsDouble() : null,
                    term.has("mean") ? term.get("mean").getAsDouble() : null);
            ranges.add(range);
        }
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public class Range {
        private Double from = Double.NEGATIVE_INFINITY;
        private Double to = Double.POSITIVE_INFINITY;
        private Long count;
        private Long totalCount;
        private Double total;
        private Double min = Double.POSITIVE_INFINITY;
        private Double max = Double.NEGATIVE_INFINITY;
        private Double mean;

        public Range(Double from, Double to, Long count, Long totalCount,
                     Double total, Double min, Double max, Double mean) {
            this.count = count;
            this.from = from;
            this.to = to;
            this.totalCount = totalCount;
            this.total = total;
            this.min = min;
            this.max = max;
            this.mean = mean;
        }

        public Double getFrom() {
            return from;
        }

        public Double getTo() {
            return to;
        }

        public Long getCount() {
            return count;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public Double getTotal() {
            return total;
        }

        public Double getMin() {
            return min;
        }

        public Double getMax() {
            return max;
        }

        public Double getMean() {
            return mean;
        }
    }

}
